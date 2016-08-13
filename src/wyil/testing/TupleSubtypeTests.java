// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

// This file was automatically generated.
package wyil.testing;
import org.junit.*;
import static org.junit.Assert.*;
import wyil.lang.Type;

public class TupleSubtypeTests {
	@Test public void test_1() { checkIsSubtype("any","any"); }
	@Test public void test_2() { checkIsSubtype("any","null"); }
	@Test public void test_3() { checkIsSubtype("any","int"); }
	@Test public void test_4() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_5() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_6() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_7() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_8() { checkIsSubtype("any","{int f1}"); }
	@Test public void test_9() { checkIsSubtype("any","{int f2}"); }
	@Test public void test_10() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_11() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_12() { checkIsSubtype("any","(any,int)"); }
	@Test public void test_13() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_14() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_15() { checkIsSubtype("any","(null,int)"); }
	@Test public void test_16() { checkIsSubtype("any","(int,any)"); }
	@Test public void test_17() { checkIsSubtype("any","(int,null)"); }
	@Test public void test_18() { checkIsSubtype("any","(int,int)"); }
	@Test public void test_19() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_20() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_21() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_22() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_23() { checkIsSubtype("any","({int f1},int)"); }
	@Test public void test_24() { checkIsSubtype("any","({int f2},int)"); }
	@Test public void test_25() { checkIsSubtype("any","{{void f1} f1}"); }
	@Test public void test_26() { checkIsSubtype("any","{{void f2} f1}"); }
	@Test public void test_27() { checkIsSubtype("any","{{void f1} f2}"); }
	@Test public void test_28() { checkIsSubtype("any","{{void f2} f2}"); }
	@Test public void test_29() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_30() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_31() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_32() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_33() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_34() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_35() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_36() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_37() { checkIsSubtype("any","{{int f1} f1}"); }
	@Test public void test_38() { checkIsSubtype("any","{{int f2} f1}"); }
	@Test public void test_39() { checkIsSubtype("any","{{int f1} f2}"); }
	@Test public void test_40() { checkIsSubtype("any","{{int f2} f2}"); }
	@Test public void test_41() { checkIsSubtype("any","any"); }
	@Test public void test_42() { checkIsSubtype("any","null"); }
	@Test public void test_43() { checkIsSubtype("any","int"); }
	@Test public void test_44() { checkIsSubtype("any","any"); }
	@Test public void test_45() { checkIsSubtype("any","any"); }
	@Test public void test_46() { checkIsSubtype("any","any"); }
	@Test public void test_47() { checkIsSubtype("any","any"); }
	@Test public void test_48() { checkIsSubtype("any","null"); }
	@Test public void test_49() { checkIsSubtype("any","any"); }
	@Test public void test_50() { checkIsSubtype("any","null"); }
	@Test public void test_51() { checkIsSubtype("any","null|int"); }
	@Test public void test_52() { checkIsSubtype("any","int"); }
	@Test public void test_53() { checkIsSubtype("any","any"); }
	@Test public void test_54() { checkIsSubtype("any","int|null"); }
	@Test public void test_55() { checkIsSubtype("any","int"); }
	@Test public void test_56() { checkIsSubtype("any","any"); }
	@Test public void test_57() { checkIsSubtype("any","any"); }
	@Test public void test_58() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_59() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_60() { checkIsSubtype("any","{int f1}|int"); }
	@Test public void test_61() { checkIsSubtype("any","{int f2}|int"); }
	@Test public void test_62() { checkNotSubtype("null","any"); }
	@Test public void test_63() { checkIsSubtype("null","null"); }
	@Test public void test_64() { checkNotSubtype("null","int"); }
	@Test public void test_65() { checkNotSubtype("null","{any f1}"); }
	@Test public void test_66() { checkNotSubtype("null","{any f2}"); }
	@Test public void test_67() { checkNotSubtype("null","{null f1}"); }
	@Test public void test_68() { checkNotSubtype("null","{null f2}"); }
	@Test public void test_69() { checkNotSubtype("null","{int f1}"); }
	@Test public void test_70() { checkNotSubtype("null","{int f2}"); }
	@Test public void test_71() { checkNotSubtype("null","(any,any)"); }
	@Test public void test_72() { checkNotSubtype("null","(any,null)"); }
	@Test public void test_73() { checkNotSubtype("null","(any,int)"); }
	@Test public void test_74() { checkNotSubtype("null","(null,any)"); }
	@Test public void test_75() { checkNotSubtype("null","(null,null)"); }
	@Test public void test_76() { checkNotSubtype("null","(null,int)"); }
	@Test public void test_77() { checkNotSubtype("null","(int,any)"); }
	@Test public void test_78() { checkNotSubtype("null","(int,null)"); }
	@Test public void test_79() { checkNotSubtype("null","(int,int)"); }
	@Test public void test_80() { checkNotSubtype("null","({any f1},any)"); }
	@Test public void test_81() { checkNotSubtype("null","({any f2},any)"); }
	@Test public void test_82() { checkNotSubtype("null","({null f1},null)"); }
	@Test public void test_83() { checkNotSubtype("null","({null f2},null)"); }
	@Test public void test_84() { checkNotSubtype("null","({int f1},int)"); }
	@Test public void test_85() { checkNotSubtype("null","({int f2},int)"); }
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
	@Test public void test_98() { checkNotSubtype("null","{{int f1} f1}"); }
	@Test public void test_99() { checkNotSubtype("null","{{int f2} f1}"); }
	@Test public void test_100() { checkNotSubtype("null","{{int f1} f2}"); }
	@Test public void test_101() { checkNotSubtype("null","{{int f2} f2}"); }
	@Test public void test_102() { checkNotSubtype("null","any"); }
	@Test public void test_103() { checkIsSubtype("null","null"); }
	@Test public void test_104() { checkNotSubtype("null","int"); }
	@Test public void test_105() { checkNotSubtype("null","any"); }
	@Test public void test_106() { checkNotSubtype("null","any"); }
	@Test public void test_107() { checkNotSubtype("null","any"); }
	@Test public void test_108() { checkNotSubtype("null","any"); }
	@Test public void test_109() { checkIsSubtype("null","null"); }
	@Test public void test_110() { checkNotSubtype("null","any"); }
	@Test public void test_111() { checkIsSubtype("null","null"); }
	@Test public void test_112() { checkNotSubtype("null","null|int"); }
	@Test public void test_113() { checkNotSubtype("null","int"); }
	@Test public void test_114() { checkNotSubtype("null","any"); }
	@Test public void test_115() { checkNotSubtype("null","int|null"); }
	@Test public void test_116() { checkNotSubtype("null","int"); }
	@Test public void test_117() { checkNotSubtype("null","any"); }
	@Test public void test_118() { checkNotSubtype("null","any"); }
	@Test public void test_119() { checkNotSubtype("null","{null f1}|null"); }
	@Test public void test_120() { checkNotSubtype("null","{null f2}|null"); }
	@Test public void test_121() { checkNotSubtype("null","{int f1}|int"); }
	@Test public void test_122() { checkNotSubtype("null","{int f2}|int"); }
	@Test public void test_123() { checkNotSubtype("int","any"); }
	@Test public void test_124() { checkNotSubtype("int","null"); }
	@Test public void test_125() { checkIsSubtype("int","int"); }
	@Test public void test_126() { checkNotSubtype("int","{any f1}"); }
	@Test public void test_127() { checkNotSubtype("int","{any f2}"); }
	@Test public void test_128() { checkNotSubtype("int","{null f1}"); }
	@Test public void test_129() { checkNotSubtype("int","{null f2}"); }
	@Test public void test_130() { checkNotSubtype("int","{int f1}"); }
	@Test public void test_131() { checkNotSubtype("int","{int f2}"); }
	@Test public void test_132() { checkNotSubtype("int","(any,any)"); }
	@Test public void test_133() { checkNotSubtype("int","(any,null)"); }
	@Test public void test_134() { checkNotSubtype("int","(any,int)"); }
	@Test public void test_135() { checkNotSubtype("int","(null,any)"); }
	@Test public void test_136() { checkNotSubtype("int","(null,null)"); }
	@Test public void test_137() { checkNotSubtype("int","(null,int)"); }
	@Test public void test_138() { checkNotSubtype("int","(int,any)"); }
	@Test public void test_139() { checkNotSubtype("int","(int,null)"); }
	@Test public void test_140() { checkNotSubtype("int","(int,int)"); }
	@Test public void test_141() { checkNotSubtype("int","({any f1},any)"); }
	@Test public void test_142() { checkNotSubtype("int","({any f2},any)"); }
	@Test public void test_143() { checkNotSubtype("int","({null f1},null)"); }
	@Test public void test_144() { checkNotSubtype("int","({null f2},null)"); }
	@Test public void test_145() { checkNotSubtype("int","({int f1},int)"); }
	@Test public void test_146() { checkNotSubtype("int","({int f2},int)"); }
	@Test public void test_147() { checkIsSubtype("int","{{void f1} f1}"); }
	@Test public void test_148() { checkIsSubtype("int","{{void f2} f1}"); }
	@Test public void test_149() { checkIsSubtype("int","{{void f1} f2}"); }
	@Test public void test_150() { checkIsSubtype("int","{{void f2} f2}"); }
	@Test public void test_151() { checkNotSubtype("int","{{any f1} f1}"); }
	@Test public void test_152() { checkNotSubtype("int","{{any f2} f1}"); }
	@Test public void test_153() { checkNotSubtype("int","{{any f1} f2}"); }
	@Test public void test_154() { checkNotSubtype("int","{{any f2} f2}"); }
	@Test public void test_155() { checkNotSubtype("int","{{null f1} f1}"); }
	@Test public void test_156() { checkNotSubtype("int","{{null f2} f1}"); }
	@Test public void test_157() { checkNotSubtype("int","{{null f1} f2}"); }
	@Test public void test_158() { checkNotSubtype("int","{{null f2} f2}"); }
	@Test public void test_159() { checkNotSubtype("int","{{int f1} f1}"); }
	@Test public void test_160() { checkNotSubtype("int","{{int f2} f1}"); }
	@Test public void test_161() { checkNotSubtype("int","{{int f1} f2}"); }
	@Test public void test_162() { checkNotSubtype("int","{{int f2} f2}"); }
	@Test public void test_163() { checkNotSubtype("int","any"); }
	@Test public void test_164() { checkNotSubtype("int","null"); }
	@Test public void test_165() { checkIsSubtype("int","int"); }
	@Test public void test_166() { checkNotSubtype("int","any"); }
	@Test public void test_167() { checkNotSubtype("int","any"); }
	@Test public void test_168() { checkNotSubtype("int","any"); }
	@Test public void test_169() { checkNotSubtype("int","any"); }
	@Test public void test_170() { checkNotSubtype("int","null"); }
	@Test public void test_171() { checkNotSubtype("int","any"); }
	@Test public void test_172() { checkNotSubtype("int","null"); }
	@Test public void test_173() { checkNotSubtype("int","null|int"); }
	@Test public void test_174() { checkIsSubtype("int","int"); }
	@Test public void test_175() { checkNotSubtype("int","any"); }
	@Test public void test_176() { checkNotSubtype("int","int|null"); }
	@Test public void test_177() { checkIsSubtype("int","int"); }
	@Test public void test_178() { checkNotSubtype("int","any"); }
	@Test public void test_179() { checkNotSubtype("int","any"); }
	@Test public void test_180() { checkNotSubtype("int","{null f1}|null"); }
	@Test public void test_181() { checkNotSubtype("int","{null f2}|null"); }
	@Test public void test_182() { checkNotSubtype("int","{int f1}|int"); }
	@Test public void test_183() { checkNotSubtype("int","{int f2}|int"); }
	@Test public void test_184() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_185() { checkNotSubtype("{any f1}","null"); }
	@Test public void test_186() { checkNotSubtype("{any f1}","int"); }
	@Test public void test_187() { checkIsSubtype("{any f1}","{any f1}"); }
	@Test public void test_188() { checkNotSubtype("{any f1}","{any f2}"); }
	@Test public void test_189() { checkIsSubtype("{any f1}","{null f1}"); }
	@Test public void test_190() { checkNotSubtype("{any f1}","{null f2}"); }
	@Test public void test_191() { checkIsSubtype("{any f1}","{int f1}"); }
	@Test public void test_192() { checkNotSubtype("{any f1}","{int f2}"); }
	@Test public void test_193() { checkNotSubtype("{any f1}","(any,any)"); }
	@Test public void test_194() { checkNotSubtype("{any f1}","(any,null)"); }
	@Test public void test_195() { checkNotSubtype("{any f1}","(any,int)"); }
	@Test public void test_196() { checkNotSubtype("{any f1}","(null,any)"); }
	@Test public void test_197() { checkNotSubtype("{any f1}","(null,null)"); }
	@Test public void test_198() { checkNotSubtype("{any f1}","(null,int)"); }
	@Test public void test_199() { checkNotSubtype("{any f1}","(int,any)"); }
	@Test public void test_200() { checkNotSubtype("{any f1}","(int,null)"); }
	@Test public void test_201() { checkNotSubtype("{any f1}","(int,int)"); }
	@Test public void test_202() { checkNotSubtype("{any f1}","({any f1},any)"); }
	@Test public void test_203() { checkNotSubtype("{any f1}","({any f2},any)"); }
	@Test public void test_204() { checkNotSubtype("{any f1}","({null f1},null)"); }
	@Test public void test_205() { checkNotSubtype("{any f1}","({null f2},null)"); }
	@Test public void test_206() { checkNotSubtype("{any f1}","({int f1},int)"); }
	@Test public void test_207() { checkNotSubtype("{any f1}","({int f2},int)"); }
	@Test public void test_208() { checkIsSubtype("{any f1}","{{void f1} f1}"); }
	@Test public void test_209() { checkIsSubtype("{any f1}","{{void f2} f1}"); }
	@Test public void test_210() { checkIsSubtype("{any f1}","{{void f1} f2}"); }
	@Test public void test_211() { checkIsSubtype("{any f1}","{{void f2} f2}"); }
	@Test public void test_212() { checkIsSubtype("{any f1}","{{any f1} f1}"); }
	@Test public void test_213() { checkIsSubtype("{any f1}","{{any f2} f1}"); }
	@Test public void test_214() { checkNotSubtype("{any f1}","{{any f1} f2}"); }
	@Test public void test_215() { checkNotSubtype("{any f1}","{{any f2} f2}"); }
	@Test public void test_216() { checkIsSubtype("{any f1}","{{null f1} f1}"); }
	@Test public void test_217() { checkIsSubtype("{any f1}","{{null f2} f1}"); }
	@Test public void test_218() { checkNotSubtype("{any f1}","{{null f1} f2}"); }
	@Test public void test_219() { checkNotSubtype("{any f1}","{{null f2} f2}"); }
	@Test public void test_220() { checkIsSubtype("{any f1}","{{int f1} f1}"); }
	@Test public void test_221() { checkIsSubtype("{any f1}","{{int f2} f1}"); }
	@Test public void test_222() { checkNotSubtype("{any f1}","{{int f1} f2}"); }
	@Test public void test_223() { checkNotSubtype("{any f1}","{{int f2} f2}"); }
	@Test public void test_224() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_225() { checkNotSubtype("{any f1}","null"); }
	@Test public void test_226() { checkNotSubtype("{any f1}","int"); }
	@Test public void test_227() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_228() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_229() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_230() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_231() { checkNotSubtype("{any f1}","null"); }
	@Test public void test_232() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_233() { checkNotSubtype("{any f1}","null"); }
	@Test public void test_234() { checkNotSubtype("{any f1}","null|int"); }
	@Test public void test_235() { checkNotSubtype("{any f1}","int"); }
	@Test public void test_236() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_237() { checkNotSubtype("{any f1}","int|null"); }
	@Test public void test_238() { checkNotSubtype("{any f1}","int"); }
	@Test public void test_239() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_240() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_241() { checkNotSubtype("{any f1}","{null f1}|null"); }
	@Test public void test_242() { checkNotSubtype("{any f1}","{null f2}|null"); }
	@Test public void test_243() { checkNotSubtype("{any f1}","{int f1}|int"); }
	@Test public void test_244() { checkNotSubtype("{any f1}","{int f2}|int"); }
	@Test public void test_245() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_246() { checkNotSubtype("{any f2}","null"); }
	@Test public void test_247() { checkNotSubtype("{any f2}","int"); }
	@Test public void test_248() { checkNotSubtype("{any f2}","{any f1}"); }
	@Test public void test_249() { checkIsSubtype("{any f2}","{any f2}"); }
	@Test public void test_250() { checkNotSubtype("{any f2}","{null f1}"); }
	@Test public void test_251() { checkIsSubtype("{any f2}","{null f2}"); }
	@Test public void test_252() { checkNotSubtype("{any f2}","{int f1}"); }
	@Test public void test_253() { checkIsSubtype("{any f2}","{int f2}"); }
	@Test public void test_254() { checkNotSubtype("{any f2}","(any,any)"); }
	@Test public void test_255() { checkNotSubtype("{any f2}","(any,null)"); }
	@Test public void test_256() { checkNotSubtype("{any f2}","(any,int)"); }
	@Test public void test_257() { checkNotSubtype("{any f2}","(null,any)"); }
	@Test public void test_258() { checkNotSubtype("{any f2}","(null,null)"); }
	@Test public void test_259() { checkNotSubtype("{any f2}","(null,int)"); }
	@Test public void test_260() { checkNotSubtype("{any f2}","(int,any)"); }
	@Test public void test_261() { checkNotSubtype("{any f2}","(int,null)"); }
	@Test public void test_262() { checkNotSubtype("{any f2}","(int,int)"); }
	@Test public void test_263() { checkNotSubtype("{any f2}","({any f1},any)"); }
	@Test public void test_264() { checkNotSubtype("{any f2}","({any f2},any)"); }
	@Test public void test_265() { checkNotSubtype("{any f2}","({null f1},null)"); }
	@Test public void test_266() { checkNotSubtype("{any f2}","({null f2},null)"); }
	@Test public void test_267() { checkNotSubtype("{any f2}","({int f1},int)"); }
	@Test public void test_268() { checkNotSubtype("{any f2}","({int f2},int)"); }
	@Test public void test_269() { checkIsSubtype("{any f2}","{{void f1} f1}"); }
	@Test public void test_270() { checkIsSubtype("{any f2}","{{void f2} f1}"); }
	@Test public void test_271() { checkIsSubtype("{any f2}","{{void f1} f2}"); }
	@Test public void test_272() { checkIsSubtype("{any f2}","{{void f2} f2}"); }
	@Test public void test_273() { checkNotSubtype("{any f2}","{{any f1} f1}"); }
	@Test public void test_274() { checkNotSubtype("{any f2}","{{any f2} f1}"); }
	@Test public void test_275() { checkIsSubtype("{any f2}","{{any f1} f2}"); }
	@Test public void test_276() { checkIsSubtype("{any f2}","{{any f2} f2}"); }
	@Test public void test_277() { checkNotSubtype("{any f2}","{{null f1} f1}"); }
	@Test public void test_278() { checkNotSubtype("{any f2}","{{null f2} f1}"); }
	@Test public void test_279() { checkIsSubtype("{any f2}","{{null f1} f2}"); }
	@Test public void test_280() { checkIsSubtype("{any f2}","{{null f2} f2}"); }
	@Test public void test_281() { checkNotSubtype("{any f2}","{{int f1} f1}"); }
	@Test public void test_282() { checkNotSubtype("{any f2}","{{int f2} f1}"); }
	@Test public void test_283() { checkIsSubtype("{any f2}","{{int f1} f2}"); }
	@Test public void test_284() { checkIsSubtype("{any f2}","{{int f2} f2}"); }
	@Test public void test_285() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_286() { checkNotSubtype("{any f2}","null"); }
	@Test public void test_287() { checkNotSubtype("{any f2}","int"); }
	@Test public void test_288() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_289() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_290() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_291() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_292() { checkNotSubtype("{any f2}","null"); }
	@Test public void test_293() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_294() { checkNotSubtype("{any f2}","null"); }
	@Test public void test_295() { checkNotSubtype("{any f2}","null|int"); }
	@Test public void test_296() { checkNotSubtype("{any f2}","int"); }
	@Test public void test_297() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_298() { checkNotSubtype("{any f2}","int|null"); }
	@Test public void test_299() { checkNotSubtype("{any f2}","int"); }
	@Test public void test_300() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_301() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_302() { checkNotSubtype("{any f2}","{null f1}|null"); }
	@Test public void test_303() { checkNotSubtype("{any f2}","{null f2}|null"); }
	@Test public void test_304() { checkNotSubtype("{any f2}","{int f1}|int"); }
	@Test public void test_305() { checkNotSubtype("{any f2}","{int f2}|int"); }
	@Test public void test_306() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_307() { checkNotSubtype("{null f1}","null"); }
	@Test public void test_308() { checkNotSubtype("{null f1}","int"); }
	@Test public void test_309() { checkNotSubtype("{null f1}","{any f1}"); }
	@Test public void test_310() { checkNotSubtype("{null f1}","{any f2}"); }
	@Test public void test_311() { checkIsSubtype("{null f1}","{null f1}"); }
	@Test public void test_312() { checkNotSubtype("{null f1}","{null f2}"); }
	@Test public void test_313() { checkNotSubtype("{null f1}","{int f1}"); }
	@Test public void test_314() { checkNotSubtype("{null f1}","{int f2}"); }
	@Test public void test_315() { checkNotSubtype("{null f1}","(any,any)"); }
	@Test public void test_316() { checkNotSubtype("{null f1}","(any,null)"); }
	@Test public void test_317() { checkNotSubtype("{null f1}","(any,int)"); }
	@Test public void test_318() { checkNotSubtype("{null f1}","(null,any)"); }
	@Test public void test_319() { checkNotSubtype("{null f1}","(null,null)"); }
	@Test public void test_320() { checkNotSubtype("{null f1}","(null,int)"); }
	@Test public void test_321() { checkNotSubtype("{null f1}","(int,any)"); }
	@Test public void test_322() { checkNotSubtype("{null f1}","(int,null)"); }
	@Test public void test_323() { checkNotSubtype("{null f1}","(int,int)"); }
	@Test public void test_324() { checkNotSubtype("{null f1}","({any f1},any)"); }
	@Test public void test_325() { checkNotSubtype("{null f1}","({any f2},any)"); }
	@Test public void test_326() { checkNotSubtype("{null f1}","({null f1},null)"); }
	@Test public void test_327() { checkNotSubtype("{null f1}","({null f2},null)"); }
	@Test public void test_328() { checkNotSubtype("{null f1}","({int f1},int)"); }
	@Test public void test_329() { checkNotSubtype("{null f1}","({int f2},int)"); }
	@Test public void test_330() { checkIsSubtype("{null f1}","{{void f1} f1}"); }
	@Test public void test_331() { checkIsSubtype("{null f1}","{{void f2} f1}"); }
	@Test public void test_332() { checkIsSubtype("{null f1}","{{void f1} f2}"); }
	@Test public void test_333() { checkIsSubtype("{null f1}","{{void f2} f2}"); }
	@Test public void test_334() { checkNotSubtype("{null f1}","{{any f1} f1}"); }
	@Test public void test_335() { checkNotSubtype("{null f1}","{{any f2} f1}"); }
	@Test public void test_336() { checkNotSubtype("{null f1}","{{any f1} f2}"); }
	@Test public void test_337() { checkNotSubtype("{null f1}","{{any f2} f2}"); }
	@Test public void test_338() { checkNotSubtype("{null f1}","{{null f1} f1}"); }
	@Test public void test_339() { checkNotSubtype("{null f1}","{{null f2} f1}"); }
	@Test public void test_340() { checkNotSubtype("{null f1}","{{null f1} f2}"); }
	@Test public void test_341() { checkNotSubtype("{null f1}","{{null f2} f2}"); }
	@Test public void test_342() { checkNotSubtype("{null f1}","{{int f1} f1}"); }
	@Test public void test_343() { checkNotSubtype("{null f1}","{{int f2} f1}"); }
	@Test public void test_344() { checkNotSubtype("{null f1}","{{int f1} f2}"); }
	@Test public void test_345() { checkNotSubtype("{null f1}","{{int f2} f2}"); }
	@Test public void test_346() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_347() { checkNotSubtype("{null f1}","null"); }
	@Test public void test_348() { checkNotSubtype("{null f1}","int"); }
	@Test public void test_349() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_350() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_351() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_352() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_353() { checkNotSubtype("{null f1}","null"); }
	@Test public void test_354() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_355() { checkNotSubtype("{null f1}","null"); }
	@Test public void test_356() { checkNotSubtype("{null f1}","null|int"); }
	@Test public void test_357() { checkNotSubtype("{null f1}","int"); }
	@Test public void test_358() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_359() { checkNotSubtype("{null f1}","int|null"); }
	@Test public void test_360() { checkNotSubtype("{null f1}","int"); }
	@Test public void test_361() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_362() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_363() { checkNotSubtype("{null f1}","{null f1}|null"); }
	@Test public void test_364() { checkNotSubtype("{null f1}","{null f2}|null"); }
	@Test public void test_365() { checkNotSubtype("{null f1}","{int f1}|int"); }
	@Test public void test_366() { checkNotSubtype("{null f1}","{int f2}|int"); }
	@Test public void test_367() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_368() { checkNotSubtype("{null f2}","null"); }
	@Test public void test_369() { checkNotSubtype("{null f2}","int"); }
	@Test public void test_370() { checkNotSubtype("{null f2}","{any f1}"); }
	@Test public void test_371() { checkNotSubtype("{null f2}","{any f2}"); }
	@Test public void test_372() { checkNotSubtype("{null f2}","{null f1}"); }
	@Test public void test_373() { checkIsSubtype("{null f2}","{null f2}"); }
	@Test public void test_374() { checkNotSubtype("{null f2}","{int f1}"); }
	@Test public void test_375() { checkNotSubtype("{null f2}","{int f2}"); }
	@Test public void test_376() { checkNotSubtype("{null f2}","(any,any)"); }
	@Test public void test_377() { checkNotSubtype("{null f2}","(any,null)"); }
	@Test public void test_378() { checkNotSubtype("{null f2}","(any,int)"); }
	@Test public void test_379() { checkNotSubtype("{null f2}","(null,any)"); }
	@Test public void test_380() { checkNotSubtype("{null f2}","(null,null)"); }
	@Test public void test_381() { checkNotSubtype("{null f2}","(null,int)"); }
	@Test public void test_382() { checkNotSubtype("{null f2}","(int,any)"); }
	@Test public void test_383() { checkNotSubtype("{null f2}","(int,null)"); }
	@Test public void test_384() { checkNotSubtype("{null f2}","(int,int)"); }
	@Test public void test_385() { checkNotSubtype("{null f2}","({any f1},any)"); }
	@Test public void test_386() { checkNotSubtype("{null f2}","({any f2},any)"); }
	@Test public void test_387() { checkNotSubtype("{null f2}","({null f1},null)"); }
	@Test public void test_388() { checkNotSubtype("{null f2}","({null f2},null)"); }
	@Test public void test_389() { checkNotSubtype("{null f2}","({int f1},int)"); }
	@Test public void test_390() { checkNotSubtype("{null f2}","({int f2},int)"); }
	@Test public void test_391() { checkIsSubtype("{null f2}","{{void f1} f1}"); }
	@Test public void test_392() { checkIsSubtype("{null f2}","{{void f2} f1}"); }
	@Test public void test_393() { checkIsSubtype("{null f2}","{{void f1} f2}"); }
	@Test public void test_394() { checkIsSubtype("{null f2}","{{void f2} f2}"); }
	@Test public void test_395() { checkNotSubtype("{null f2}","{{any f1} f1}"); }
	@Test public void test_396() { checkNotSubtype("{null f2}","{{any f2} f1}"); }
	@Test public void test_397() { checkNotSubtype("{null f2}","{{any f1} f2}"); }
	@Test public void test_398() { checkNotSubtype("{null f2}","{{any f2} f2}"); }
	@Test public void test_399() { checkNotSubtype("{null f2}","{{null f1} f1}"); }
	@Test public void test_400() { checkNotSubtype("{null f2}","{{null f2} f1}"); }
	@Test public void test_401() { checkNotSubtype("{null f2}","{{null f1} f2}"); }
	@Test public void test_402() { checkNotSubtype("{null f2}","{{null f2} f2}"); }
	@Test public void test_403() { checkNotSubtype("{null f2}","{{int f1} f1}"); }
	@Test public void test_404() { checkNotSubtype("{null f2}","{{int f2} f1}"); }
	@Test public void test_405() { checkNotSubtype("{null f2}","{{int f1} f2}"); }
	@Test public void test_406() { checkNotSubtype("{null f2}","{{int f2} f2}"); }
	@Test public void test_407() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_408() { checkNotSubtype("{null f2}","null"); }
	@Test public void test_409() { checkNotSubtype("{null f2}","int"); }
	@Test public void test_410() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_411() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_412() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_413() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_414() { checkNotSubtype("{null f2}","null"); }
	@Test public void test_415() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_416() { checkNotSubtype("{null f2}","null"); }
	@Test public void test_417() { checkNotSubtype("{null f2}","null|int"); }
	@Test public void test_418() { checkNotSubtype("{null f2}","int"); }
	@Test public void test_419() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_420() { checkNotSubtype("{null f2}","int|null"); }
	@Test public void test_421() { checkNotSubtype("{null f2}","int"); }
	@Test public void test_422() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_423() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_424() { checkNotSubtype("{null f2}","{null f1}|null"); }
	@Test public void test_425() { checkNotSubtype("{null f2}","{null f2}|null"); }
	@Test public void test_426() { checkNotSubtype("{null f2}","{int f1}|int"); }
	@Test public void test_427() { checkNotSubtype("{null f2}","{int f2}|int"); }
	@Test public void test_428() { checkNotSubtype("{int f1}","any"); }
	@Test public void test_429() { checkNotSubtype("{int f1}","null"); }
	@Test public void test_430() { checkNotSubtype("{int f1}","int"); }
	@Test public void test_431() { checkNotSubtype("{int f1}","{any f1}"); }
	@Test public void test_432() { checkNotSubtype("{int f1}","{any f2}"); }
	@Test public void test_433() { checkNotSubtype("{int f1}","{null f1}"); }
	@Test public void test_434() { checkNotSubtype("{int f1}","{null f2}"); }
	@Test public void test_435() { checkIsSubtype("{int f1}","{int f1}"); }
	@Test public void test_436() { checkNotSubtype("{int f1}","{int f2}"); }
	@Test public void test_437() { checkNotSubtype("{int f1}","(any,any)"); }
	@Test public void test_438() { checkNotSubtype("{int f1}","(any,null)"); }
	@Test public void test_439() { checkNotSubtype("{int f1}","(any,int)"); }
	@Test public void test_440() { checkNotSubtype("{int f1}","(null,any)"); }
	@Test public void test_441() { checkNotSubtype("{int f1}","(null,null)"); }
	@Test public void test_442() { checkNotSubtype("{int f1}","(null,int)"); }
	@Test public void test_443() { checkNotSubtype("{int f1}","(int,any)"); }
	@Test public void test_444() { checkNotSubtype("{int f1}","(int,null)"); }
	@Test public void test_445() { checkNotSubtype("{int f1}","(int,int)"); }
	@Test public void test_446() { checkNotSubtype("{int f1}","({any f1},any)"); }
	@Test public void test_447() { checkNotSubtype("{int f1}","({any f2},any)"); }
	@Test public void test_448() { checkNotSubtype("{int f1}","({null f1},null)"); }
	@Test public void test_449() { checkNotSubtype("{int f1}","({null f2},null)"); }
	@Test public void test_450() { checkNotSubtype("{int f1}","({int f1},int)"); }
	@Test public void test_451() { checkNotSubtype("{int f1}","({int f2},int)"); }
	@Test public void test_452() { checkIsSubtype("{int f1}","{{void f1} f1}"); }
	@Test public void test_453() { checkIsSubtype("{int f1}","{{void f2} f1}"); }
	@Test public void test_454() { checkIsSubtype("{int f1}","{{void f1} f2}"); }
	@Test public void test_455() { checkIsSubtype("{int f1}","{{void f2} f2}"); }
	@Test public void test_456() { checkNotSubtype("{int f1}","{{any f1} f1}"); }
	@Test public void test_457() { checkNotSubtype("{int f1}","{{any f2} f1}"); }
	@Test public void test_458() { checkNotSubtype("{int f1}","{{any f1} f2}"); }
	@Test public void test_459() { checkNotSubtype("{int f1}","{{any f2} f2}"); }
	@Test public void test_460() { checkNotSubtype("{int f1}","{{null f1} f1}"); }
	@Test public void test_461() { checkNotSubtype("{int f1}","{{null f2} f1}"); }
	@Test public void test_462() { checkNotSubtype("{int f1}","{{null f1} f2}"); }
	@Test public void test_463() { checkNotSubtype("{int f1}","{{null f2} f2}"); }
	@Test public void test_464() { checkNotSubtype("{int f1}","{{int f1} f1}"); }
	@Test public void test_465() { checkNotSubtype("{int f1}","{{int f2} f1}"); }
	@Test public void test_466() { checkNotSubtype("{int f1}","{{int f1} f2}"); }
	@Test public void test_467() { checkNotSubtype("{int f1}","{{int f2} f2}"); }
	@Test public void test_468() { checkNotSubtype("{int f1}","any"); }
	@Test public void test_469() { checkNotSubtype("{int f1}","null"); }
	@Test public void test_470() { checkNotSubtype("{int f1}","int"); }
	@Test public void test_471() { checkNotSubtype("{int f1}","any"); }
	@Test public void test_472() { checkNotSubtype("{int f1}","any"); }
	@Test public void test_473() { checkNotSubtype("{int f1}","any"); }
	@Test public void test_474() { checkNotSubtype("{int f1}","any"); }
	@Test public void test_475() { checkNotSubtype("{int f1}","null"); }
	@Test public void test_476() { checkNotSubtype("{int f1}","any"); }
	@Test public void test_477() { checkNotSubtype("{int f1}","null"); }
	@Test public void test_478() { checkNotSubtype("{int f1}","null|int"); }
	@Test public void test_479() { checkNotSubtype("{int f1}","int"); }
	@Test public void test_480() { checkNotSubtype("{int f1}","any"); }
	@Test public void test_481() { checkNotSubtype("{int f1}","int|null"); }
	@Test public void test_482() { checkNotSubtype("{int f1}","int"); }
	@Test public void test_483() { checkNotSubtype("{int f1}","any"); }
	@Test public void test_484() { checkNotSubtype("{int f1}","any"); }
	@Test public void test_485() { checkNotSubtype("{int f1}","{null f1}|null"); }
	@Test public void test_486() { checkNotSubtype("{int f1}","{null f2}|null"); }
	@Test public void test_487() { checkNotSubtype("{int f1}","{int f1}|int"); }
	@Test public void test_488() { checkNotSubtype("{int f1}","{int f2}|int"); }
	@Test public void test_489() { checkNotSubtype("{int f2}","any"); }
	@Test public void test_490() { checkNotSubtype("{int f2}","null"); }
	@Test public void test_491() { checkNotSubtype("{int f2}","int"); }
	@Test public void test_492() { checkNotSubtype("{int f2}","{any f1}"); }
	@Test public void test_493() { checkNotSubtype("{int f2}","{any f2}"); }
	@Test public void test_494() { checkNotSubtype("{int f2}","{null f1}"); }
	@Test public void test_495() { checkNotSubtype("{int f2}","{null f2}"); }
	@Test public void test_496() { checkNotSubtype("{int f2}","{int f1}"); }
	@Test public void test_497() { checkIsSubtype("{int f2}","{int f2}"); }
	@Test public void test_498() { checkNotSubtype("{int f2}","(any,any)"); }
	@Test public void test_499() { checkNotSubtype("{int f2}","(any,null)"); }
	@Test public void test_500() { checkNotSubtype("{int f2}","(any,int)"); }
	@Test public void test_501() { checkNotSubtype("{int f2}","(null,any)"); }
	@Test public void test_502() { checkNotSubtype("{int f2}","(null,null)"); }
	@Test public void test_503() { checkNotSubtype("{int f2}","(null,int)"); }
	@Test public void test_504() { checkNotSubtype("{int f2}","(int,any)"); }
	@Test public void test_505() { checkNotSubtype("{int f2}","(int,null)"); }
	@Test public void test_506() { checkNotSubtype("{int f2}","(int,int)"); }
	@Test public void test_507() { checkNotSubtype("{int f2}","({any f1},any)"); }
	@Test public void test_508() { checkNotSubtype("{int f2}","({any f2},any)"); }
	@Test public void test_509() { checkNotSubtype("{int f2}","({null f1},null)"); }
	@Test public void test_510() { checkNotSubtype("{int f2}","({null f2},null)"); }
	@Test public void test_511() { checkNotSubtype("{int f2}","({int f1},int)"); }
	@Test public void test_512() { checkNotSubtype("{int f2}","({int f2},int)"); }
	@Test public void test_513() { checkIsSubtype("{int f2}","{{void f1} f1}"); }
	@Test public void test_514() { checkIsSubtype("{int f2}","{{void f2} f1}"); }
	@Test public void test_515() { checkIsSubtype("{int f2}","{{void f1} f2}"); }
	@Test public void test_516() { checkIsSubtype("{int f2}","{{void f2} f2}"); }
	@Test public void test_517() { checkNotSubtype("{int f2}","{{any f1} f1}"); }
	@Test public void test_518() { checkNotSubtype("{int f2}","{{any f2} f1}"); }
	@Test public void test_519() { checkNotSubtype("{int f2}","{{any f1} f2}"); }
	@Test public void test_520() { checkNotSubtype("{int f2}","{{any f2} f2}"); }
	@Test public void test_521() { checkNotSubtype("{int f2}","{{null f1} f1}"); }
	@Test public void test_522() { checkNotSubtype("{int f2}","{{null f2} f1}"); }
	@Test public void test_523() { checkNotSubtype("{int f2}","{{null f1} f2}"); }
	@Test public void test_524() { checkNotSubtype("{int f2}","{{null f2} f2}"); }
	@Test public void test_525() { checkNotSubtype("{int f2}","{{int f1} f1}"); }
	@Test public void test_526() { checkNotSubtype("{int f2}","{{int f2} f1}"); }
	@Test public void test_527() { checkNotSubtype("{int f2}","{{int f1} f2}"); }
	@Test public void test_528() { checkNotSubtype("{int f2}","{{int f2} f2}"); }
	@Test public void test_529() { checkNotSubtype("{int f2}","any"); }
	@Test public void test_530() { checkNotSubtype("{int f2}","null"); }
	@Test public void test_531() { checkNotSubtype("{int f2}","int"); }
	@Test public void test_532() { checkNotSubtype("{int f2}","any"); }
	@Test public void test_533() { checkNotSubtype("{int f2}","any"); }
	@Test public void test_534() { checkNotSubtype("{int f2}","any"); }
	@Test public void test_535() { checkNotSubtype("{int f2}","any"); }
	@Test public void test_536() { checkNotSubtype("{int f2}","null"); }
	@Test public void test_537() { checkNotSubtype("{int f2}","any"); }
	@Test public void test_538() { checkNotSubtype("{int f2}","null"); }
	@Test public void test_539() { checkNotSubtype("{int f2}","null|int"); }
	@Test public void test_540() { checkNotSubtype("{int f2}","int"); }
	@Test public void test_541() { checkNotSubtype("{int f2}","any"); }
	@Test public void test_542() { checkNotSubtype("{int f2}","int|null"); }
	@Test public void test_543() { checkNotSubtype("{int f2}","int"); }
	@Test public void test_544() { checkNotSubtype("{int f2}","any"); }
	@Test public void test_545() { checkNotSubtype("{int f2}","any"); }
	@Test public void test_546() { checkNotSubtype("{int f2}","{null f1}|null"); }
	@Test public void test_547() { checkNotSubtype("{int f2}","{null f2}|null"); }
	@Test public void test_548() { checkNotSubtype("{int f2}","{int f1}|int"); }
	@Test public void test_549() { checkNotSubtype("{int f2}","{int f2}|int"); }
	@Test public void test_550() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_551() { checkNotSubtype("(any,any)","null"); }
	@Test public void test_552() { checkNotSubtype("(any,any)","int"); }
	@Test public void test_553() { checkNotSubtype("(any,any)","{any f1}"); }
	@Test public void test_554() { checkNotSubtype("(any,any)","{any f2}"); }
	@Test public void test_555() { checkNotSubtype("(any,any)","{null f1}"); }
	@Test public void test_556() { checkNotSubtype("(any,any)","{null f2}"); }
	@Test public void test_557() { checkNotSubtype("(any,any)","{int f1}"); }
	@Test public void test_558() { checkNotSubtype("(any,any)","{int f2}"); }
	@Test public void test_559() { checkIsSubtype("(any,any)","(any,any)"); }
	@Test public void test_560() { checkIsSubtype("(any,any)","(any,null)"); }
	@Test public void test_561() { checkIsSubtype("(any,any)","(any,int)"); }
	@Test public void test_562() { checkIsSubtype("(any,any)","(null,any)"); }
	@Test public void test_563() { checkIsSubtype("(any,any)","(null,null)"); }
	@Test public void test_564() { checkIsSubtype("(any,any)","(null,int)"); }
	@Test public void test_565() { checkIsSubtype("(any,any)","(int,any)"); }
	@Test public void test_566() { checkIsSubtype("(any,any)","(int,null)"); }
	@Test public void test_567() { checkIsSubtype("(any,any)","(int,int)"); }
	@Test public void test_568() { checkIsSubtype("(any,any)","({any f1},any)"); }
	@Test public void test_569() { checkIsSubtype("(any,any)","({any f2},any)"); }
	@Test public void test_570() { checkIsSubtype("(any,any)","({null f1},null)"); }
	@Test public void test_571() { checkIsSubtype("(any,any)","({null f2},null)"); }
	@Test public void test_572() { checkIsSubtype("(any,any)","({int f1},int)"); }
	@Test public void test_573() { checkIsSubtype("(any,any)","({int f2},int)"); }
	@Test public void test_574() { checkIsSubtype("(any,any)","{{void f1} f1}"); }
	@Test public void test_575() { checkIsSubtype("(any,any)","{{void f2} f1}"); }
	@Test public void test_576() { checkIsSubtype("(any,any)","{{void f1} f2}"); }
	@Test public void test_577() { checkIsSubtype("(any,any)","{{void f2} f2}"); }
	@Test public void test_578() { checkNotSubtype("(any,any)","{{any f1} f1}"); }
	@Test public void test_579() { checkNotSubtype("(any,any)","{{any f2} f1}"); }
	@Test public void test_580() { checkNotSubtype("(any,any)","{{any f1} f2}"); }
	@Test public void test_581() { checkNotSubtype("(any,any)","{{any f2} f2}"); }
	@Test public void test_582() { checkNotSubtype("(any,any)","{{null f1} f1}"); }
	@Test public void test_583() { checkNotSubtype("(any,any)","{{null f2} f1}"); }
	@Test public void test_584() { checkNotSubtype("(any,any)","{{null f1} f2}"); }
	@Test public void test_585() { checkNotSubtype("(any,any)","{{null f2} f2}"); }
	@Test public void test_586() { checkNotSubtype("(any,any)","{{int f1} f1}"); }
	@Test public void test_587() { checkNotSubtype("(any,any)","{{int f2} f1}"); }
	@Test public void test_588() { checkNotSubtype("(any,any)","{{int f1} f2}"); }
	@Test public void test_589() { checkNotSubtype("(any,any)","{{int f2} f2}"); }
	@Test public void test_590() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_591() { checkNotSubtype("(any,any)","null"); }
	@Test public void test_592() { checkNotSubtype("(any,any)","int"); }
	@Test public void test_593() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_594() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_595() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_596() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_597() { checkNotSubtype("(any,any)","null"); }
	@Test public void test_598() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_599() { checkNotSubtype("(any,any)","null"); }
	@Test public void test_600() { checkNotSubtype("(any,any)","null|int"); }
	@Test public void test_601() { checkNotSubtype("(any,any)","int"); }
	@Test public void test_602() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_603() { checkNotSubtype("(any,any)","int|null"); }
	@Test public void test_604() { checkNotSubtype("(any,any)","int"); }
	@Test public void test_605() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_606() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_607() { checkNotSubtype("(any,any)","{null f1}|null"); }
	@Test public void test_608() { checkNotSubtype("(any,any)","{null f2}|null"); }
	@Test public void test_609() { checkNotSubtype("(any,any)","{int f1}|int"); }
	@Test public void test_610() { checkNotSubtype("(any,any)","{int f2}|int"); }
	@Test public void test_611() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_612() { checkNotSubtype("(any,null)","null"); }
	@Test public void test_613() { checkNotSubtype("(any,null)","int"); }
	@Test public void test_614() { checkNotSubtype("(any,null)","{any f1}"); }
	@Test public void test_615() { checkNotSubtype("(any,null)","{any f2}"); }
	@Test public void test_616() { checkNotSubtype("(any,null)","{null f1}"); }
	@Test public void test_617() { checkNotSubtype("(any,null)","{null f2}"); }
	@Test public void test_618() { checkNotSubtype("(any,null)","{int f1}"); }
	@Test public void test_619() { checkNotSubtype("(any,null)","{int f2}"); }
	@Test public void test_620() { checkNotSubtype("(any,null)","(any,any)"); }
	@Test public void test_621() { checkIsSubtype("(any,null)","(any,null)"); }
	@Test public void test_622() { checkNotSubtype("(any,null)","(any,int)"); }
	@Test public void test_623() { checkNotSubtype("(any,null)","(null,any)"); }
	@Test public void test_624() { checkIsSubtype("(any,null)","(null,null)"); }
	@Test public void test_625() { checkNotSubtype("(any,null)","(null,int)"); }
	@Test public void test_626() { checkNotSubtype("(any,null)","(int,any)"); }
	@Test public void test_627() { checkIsSubtype("(any,null)","(int,null)"); }
	@Test public void test_628() { checkNotSubtype("(any,null)","(int,int)"); }
	@Test public void test_629() { checkNotSubtype("(any,null)","({any f1},any)"); }
	@Test public void test_630() { checkNotSubtype("(any,null)","({any f2},any)"); }
	@Test public void test_631() { checkIsSubtype("(any,null)","({null f1},null)"); }
	@Test public void test_632() { checkIsSubtype("(any,null)","({null f2},null)"); }
	@Test public void test_633() { checkNotSubtype("(any,null)","({int f1},int)"); }
	@Test public void test_634() { checkNotSubtype("(any,null)","({int f2},int)"); }
	@Test public void test_635() { checkIsSubtype("(any,null)","{{void f1} f1}"); }
	@Test public void test_636() { checkIsSubtype("(any,null)","{{void f2} f1}"); }
	@Test public void test_637() { checkIsSubtype("(any,null)","{{void f1} f2}"); }
	@Test public void test_638() { checkIsSubtype("(any,null)","{{void f2} f2}"); }
	@Test public void test_639() { checkNotSubtype("(any,null)","{{any f1} f1}"); }
	@Test public void test_640() { checkNotSubtype("(any,null)","{{any f2} f1}"); }
	@Test public void test_641() { checkNotSubtype("(any,null)","{{any f1} f2}"); }
	@Test public void test_642() { checkNotSubtype("(any,null)","{{any f2} f2}"); }
	@Test public void test_643() { checkNotSubtype("(any,null)","{{null f1} f1}"); }
	@Test public void test_644() { checkNotSubtype("(any,null)","{{null f2} f1}"); }
	@Test public void test_645() { checkNotSubtype("(any,null)","{{null f1} f2}"); }
	@Test public void test_646() { checkNotSubtype("(any,null)","{{null f2} f2}"); }
	@Test public void test_647() { checkNotSubtype("(any,null)","{{int f1} f1}"); }
	@Test public void test_648() { checkNotSubtype("(any,null)","{{int f2} f1}"); }
	@Test public void test_649() { checkNotSubtype("(any,null)","{{int f1} f2}"); }
	@Test public void test_650() { checkNotSubtype("(any,null)","{{int f2} f2}"); }
	@Test public void test_651() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_652() { checkNotSubtype("(any,null)","null"); }
	@Test public void test_653() { checkNotSubtype("(any,null)","int"); }
	@Test public void test_654() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_655() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_656() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_657() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_658() { checkNotSubtype("(any,null)","null"); }
	@Test public void test_659() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_660() { checkNotSubtype("(any,null)","null"); }
	@Test public void test_661() { checkNotSubtype("(any,null)","null|int"); }
	@Test public void test_662() { checkNotSubtype("(any,null)","int"); }
	@Test public void test_663() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_664() { checkNotSubtype("(any,null)","int|null"); }
	@Test public void test_665() { checkNotSubtype("(any,null)","int"); }
	@Test public void test_666() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_667() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_668() { checkNotSubtype("(any,null)","{null f1}|null"); }
	@Test public void test_669() { checkNotSubtype("(any,null)","{null f2}|null"); }
	@Test public void test_670() { checkNotSubtype("(any,null)","{int f1}|int"); }
	@Test public void test_671() { checkNotSubtype("(any,null)","{int f2}|int"); }
	@Test public void test_672() { checkNotSubtype("(any,int)","any"); }
	@Test public void test_673() { checkNotSubtype("(any,int)","null"); }
	@Test public void test_674() { checkNotSubtype("(any,int)","int"); }
	@Test public void test_675() { checkNotSubtype("(any,int)","{any f1}"); }
	@Test public void test_676() { checkNotSubtype("(any,int)","{any f2}"); }
	@Test public void test_677() { checkNotSubtype("(any,int)","{null f1}"); }
	@Test public void test_678() { checkNotSubtype("(any,int)","{null f2}"); }
	@Test public void test_679() { checkNotSubtype("(any,int)","{int f1}"); }
	@Test public void test_680() { checkNotSubtype("(any,int)","{int f2}"); }
	@Test public void test_681() { checkNotSubtype("(any,int)","(any,any)"); }
	@Test public void test_682() { checkNotSubtype("(any,int)","(any,null)"); }
	@Test public void test_683() { checkIsSubtype("(any,int)","(any,int)"); }
	@Test public void test_684() { checkNotSubtype("(any,int)","(null,any)"); }
	@Test public void test_685() { checkNotSubtype("(any,int)","(null,null)"); }
	@Test public void test_686() { checkIsSubtype("(any,int)","(null,int)"); }
	@Test public void test_687() { checkNotSubtype("(any,int)","(int,any)"); }
	@Test public void test_688() { checkNotSubtype("(any,int)","(int,null)"); }
	@Test public void test_689() { checkIsSubtype("(any,int)","(int,int)"); }
	@Test public void test_690() { checkNotSubtype("(any,int)","({any f1},any)"); }
	@Test public void test_691() { checkNotSubtype("(any,int)","({any f2},any)"); }
	@Test public void test_692() { checkNotSubtype("(any,int)","({null f1},null)"); }
	@Test public void test_693() { checkNotSubtype("(any,int)","({null f2},null)"); }
	@Test public void test_694() { checkIsSubtype("(any,int)","({int f1},int)"); }
	@Test public void test_695() { checkIsSubtype("(any,int)","({int f2},int)"); }
	@Test public void test_696() { checkIsSubtype("(any,int)","{{void f1} f1}"); }
	@Test public void test_697() { checkIsSubtype("(any,int)","{{void f2} f1}"); }
	@Test public void test_698() { checkIsSubtype("(any,int)","{{void f1} f2}"); }
	@Test public void test_699() { checkIsSubtype("(any,int)","{{void f2} f2}"); }
	@Test public void test_700() { checkNotSubtype("(any,int)","{{any f1} f1}"); }
	@Test public void test_701() { checkNotSubtype("(any,int)","{{any f2} f1}"); }
	@Test public void test_702() { checkNotSubtype("(any,int)","{{any f1} f2}"); }
	@Test public void test_703() { checkNotSubtype("(any,int)","{{any f2} f2}"); }
	@Test public void test_704() { checkNotSubtype("(any,int)","{{null f1} f1}"); }
	@Test public void test_705() { checkNotSubtype("(any,int)","{{null f2} f1}"); }
	@Test public void test_706() { checkNotSubtype("(any,int)","{{null f1} f2}"); }
	@Test public void test_707() { checkNotSubtype("(any,int)","{{null f2} f2}"); }
	@Test public void test_708() { checkNotSubtype("(any,int)","{{int f1} f1}"); }
	@Test public void test_709() { checkNotSubtype("(any,int)","{{int f2} f1}"); }
	@Test public void test_710() { checkNotSubtype("(any,int)","{{int f1} f2}"); }
	@Test public void test_711() { checkNotSubtype("(any,int)","{{int f2} f2}"); }
	@Test public void test_712() { checkNotSubtype("(any,int)","any"); }
	@Test public void test_713() { checkNotSubtype("(any,int)","null"); }
	@Test public void test_714() { checkNotSubtype("(any,int)","int"); }
	@Test public void test_715() { checkNotSubtype("(any,int)","any"); }
	@Test public void test_716() { checkNotSubtype("(any,int)","any"); }
	@Test public void test_717() { checkNotSubtype("(any,int)","any"); }
	@Test public void test_718() { checkNotSubtype("(any,int)","any"); }
	@Test public void test_719() { checkNotSubtype("(any,int)","null"); }
	@Test public void test_720() { checkNotSubtype("(any,int)","any"); }
	@Test public void test_721() { checkNotSubtype("(any,int)","null"); }
	@Test public void test_722() { checkNotSubtype("(any,int)","null|int"); }
	@Test public void test_723() { checkNotSubtype("(any,int)","int"); }
	@Test public void test_724() { checkNotSubtype("(any,int)","any"); }
	@Test public void test_725() { checkNotSubtype("(any,int)","int|null"); }
	@Test public void test_726() { checkNotSubtype("(any,int)","int"); }
	@Test public void test_727() { checkNotSubtype("(any,int)","any"); }
	@Test public void test_728() { checkNotSubtype("(any,int)","any"); }
	@Test public void test_729() { checkNotSubtype("(any,int)","{null f1}|null"); }
	@Test public void test_730() { checkNotSubtype("(any,int)","{null f2}|null"); }
	@Test public void test_731() { checkNotSubtype("(any,int)","{int f1}|int"); }
	@Test public void test_732() { checkNotSubtype("(any,int)","{int f2}|int"); }
	@Test public void test_733() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_734() { checkNotSubtype("(null,any)","null"); }
	@Test public void test_735() { checkNotSubtype("(null,any)","int"); }
	@Test public void test_736() { checkNotSubtype("(null,any)","{any f1}"); }
	@Test public void test_737() { checkNotSubtype("(null,any)","{any f2}"); }
	@Test public void test_738() { checkNotSubtype("(null,any)","{null f1}"); }
	@Test public void test_739() { checkNotSubtype("(null,any)","{null f2}"); }
	@Test public void test_740() { checkNotSubtype("(null,any)","{int f1}"); }
	@Test public void test_741() { checkNotSubtype("(null,any)","{int f2}"); }
	@Test public void test_742() { checkNotSubtype("(null,any)","(any,any)"); }
	@Test public void test_743() { checkNotSubtype("(null,any)","(any,null)"); }
	@Test public void test_744() { checkNotSubtype("(null,any)","(any,int)"); }
	@Test public void test_745() { checkIsSubtype("(null,any)","(null,any)"); }
	@Test public void test_746() { checkIsSubtype("(null,any)","(null,null)"); }
	@Test public void test_747() { checkIsSubtype("(null,any)","(null,int)"); }
	@Test public void test_748() { checkNotSubtype("(null,any)","(int,any)"); }
	@Test public void test_749() { checkNotSubtype("(null,any)","(int,null)"); }
	@Test public void test_750() { checkNotSubtype("(null,any)","(int,int)"); }
	@Test public void test_751() { checkNotSubtype("(null,any)","({any f1},any)"); }
	@Test public void test_752() { checkNotSubtype("(null,any)","({any f2},any)"); }
	@Test public void test_753() { checkNotSubtype("(null,any)","({null f1},null)"); }
	@Test public void test_754() { checkNotSubtype("(null,any)","({null f2},null)"); }
	@Test public void test_755() { checkNotSubtype("(null,any)","({int f1},int)"); }
	@Test public void test_756() { checkNotSubtype("(null,any)","({int f2},int)"); }
	@Test public void test_757() { checkIsSubtype("(null,any)","{{void f1} f1}"); }
	@Test public void test_758() { checkIsSubtype("(null,any)","{{void f2} f1}"); }
	@Test public void test_759() { checkIsSubtype("(null,any)","{{void f1} f2}"); }
	@Test public void test_760() { checkIsSubtype("(null,any)","{{void f2} f2}"); }
	@Test public void test_761() { checkNotSubtype("(null,any)","{{any f1} f1}"); }
	@Test public void test_762() { checkNotSubtype("(null,any)","{{any f2} f1}"); }
	@Test public void test_763() { checkNotSubtype("(null,any)","{{any f1} f2}"); }
	@Test public void test_764() { checkNotSubtype("(null,any)","{{any f2} f2}"); }
	@Test public void test_765() { checkNotSubtype("(null,any)","{{null f1} f1}"); }
	@Test public void test_766() { checkNotSubtype("(null,any)","{{null f2} f1}"); }
	@Test public void test_767() { checkNotSubtype("(null,any)","{{null f1} f2}"); }
	@Test public void test_768() { checkNotSubtype("(null,any)","{{null f2} f2}"); }
	@Test public void test_769() { checkNotSubtype("(null,any)","{{int f1} f1}"); }
	@Test public void test_770() { checkNotSubtype("(null,any)","{{int f2} f1}"); }
	@Test public void test_771() { checkNotSubtype("(null,any)","{{int f1} f2}"); }
	@Test public void test_772() { checkNotSubtype("(null,any)","{{int f2} f2}"); }
	@Test public void test_773() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_774() { checkNotSubtype("(null,any)","null"); }
	@Test public void test_775() { checkNotSubtype("(null,any)","int"); }
	@Test public void test_776() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_777() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_778() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_779() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_780() { checkNotSubtype("(null,any)","null"); }
	@Test public void test_781() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_782() { checkNotSubtype("(null,any)","null"); }
	@Test public void test_783() { checkNotSubtype("(null,any)","null|int"); }
	@Test public void test_784() { checkNotSubtype("(null,any)","int"); }
	@Test public void test_785() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_786() { checkNotSubtype("(null,any)","int|null"); }
	@Test public void test_787() { checkNotSubtype("(null,any)","int"); }
	@Test public void test_788() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_789() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_790() { checkNotSubtype("(null,any)","{null f1}|null"); }
	@Test public void test_791() { checkNotSubtype("(null,any)","{null f2}|null"); }
	@Test public void test_792() { checkNotSubtype("(null,any)","{int f1}|int"); }
	@Test public void test_793() { checkNotSubtype("(null,any)","{int f2}|int"); }
	@Test public void test_794() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_795() { checkNotSubtype("(null,null)","null"); }
	@Test public void test_796() { checkNotSubtype("(null,null)","int"); }
	@Test public void test_797() { checkNotSubtype("(null,null)","{any f1}"); }
	@Test public void test_798() { checkNotSubtype("(null,null)","{any f2}"); }
	@Test public void test_799() { checkNotSubtype("(null,null)","{null f1}"); }
	@Test public void test_800() { checkNotSubtype("(null,null)","{null f2}"); }
	@Test public void test_801() { checkNotSubtype("(null,null)","{int f1}"); }
	@Test public void test_802() { checkNotSubtype("(null,null)","{int f2}"); }
	@Test public void test_803() { checkNotSubtype("(null,null)","(any,any)"); }
	@Test public void test_804() { checkNotSubtype("(null,null)","(any,null)"); }
	@Test public void test_805() { checkNotSubtype("(null,null)","(any,int)"); }
	@Test public void test_806() { checkNotSubtype("(null,null)","(null,any)"); }
	@Test public void test_807() { checkIsSubtype("(null,null)","(null,null)"); }
	@Test public void test_808() { checkNotSubtype("(null,null)","(null,int)"); }
	@Test public void test_809() { checkNotSubtype("(null,null)","(int,any)"); }
	@Test public void test_810() { checkNotSubtype("(null,null)","(int,null)"); }
	@Test public void test_811() { checkNotSubtype("(null,null)","(int,int)"); }
	@Test public void test_812() { checkNotSubtype("(null,null)","({any f1},any)"); }
	@Test public void test_813() { checkNotSubtype("(null,null)","({any f2},any)"); }
	@Test public void test_814() { checkNotSubtype("(null,null)","({null f1},null)"); }
	@Test public void test_815() { checkNotSubtype("(null,null)","({null f2},null)"); }
	@Test public void test_816() { checkNotSubtype("(null,null)","({int f1},int)"); }
	@Test public void test_817() { checkNotSubtype("(null,null)","({int f2},int)"); }
	@Test public void test_818() { checkIsSubtype("(null,null)","{{void f1} f1}"); }
	@Test public void test_819() { checkIsSubtype("(null,null)","{{void f2} f1}"); }
	@Test public void test_820() { checkIsSubtype("(null,null)","{{void f1} f2}"); }
	@Test public void test_821() { checkIsSubtype("(null,null)","{{void f2} f2}"); }
	@Test public void test_822() { checkNotSubtype("(null,null)","{{any f1} f1}"); }
	@Test public void test_823() { checkNotSubtype("(null,null)","{{any f2} f1}"); }
	@Test public void test_824() { checkNotSubtype("(null,null)","{{any f1} f2}"); }
	@Test public void test_825() { checkNotSubtype("(null,null)","{{any f2} f2}"); }
	@Test public void test_826() { checkNotSubtype("(null,null)","{{null f1} f1}"); }
	@Test public void test_827() { checkNotSubtype("(null,null)","{{null f2} f1}"); }
	@Test public void test_828() { checkNotSubtype("(null,null)","{{null f1} f2}"); }
	@Test public void test_829() { checkNotSubtype("(null,null)","{{null f2} f2}"); }
	@Test public void test_830() { checkNotSubtype("(null,null)","{{int f1} f1}"); }
	@Test public void test_831() { checkNotSubtype("(null,null)","{{int f2} f1}"); }
	@Test public void test_832() { checkNotSubtype("(null,null)","{{int f1} f2}"); }
	@Test public void test_833() { checkNotSubtype("(null,null)","{{int f2} f2}"); }
	@Test public void test_834() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_835() { checkNotSubtype("(null,null)","null"); }
	@Test public void test_836() { checkNotSubtype("(null,null)","int"); }
	@Test public void test_837() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_838() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_839() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_840() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_841() { checkNotSubtype("(null,null)","null"); }
	@Test public void test_842() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_843() { checkNotSubtype("(null,null)","null"); }
	@Test public void test_844() { checkNotSubtype("(null,null)","null|int"); }
	@Test public void test_845() { checkNotSubtype("(null,null)","int"); }
	@Test public void test_846() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_847() { checkNotSubtype("(null,null)","int|null"); }
	@Test public void test_848() { checkNotSubtype("(null,null)","int"); }
	@Test public void test_849() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_850() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_851() { checkNotSubtype("(null,null)","{null f1}|null"); }
	@Test public void test_852() { checkNotSubtype("(null,null)","{null f2}|null"); }
	@Test public void test_853() { checkNotSubtype("(null,null)","{int f1}|int"); }
	@Test public void test_854() { checkNotSubtype("(null,null)","{int f2}|int"); }
	@Test public void test_855() { checkNotSubtype("(null,int)","any"); }
	@Test public void test_856() { checkNotSubtype("(null,int)","null"); }
	@Test public void test_857() { checkNotSubtype("(null,int)","int"); }
	@Test public void test_858() { checkNotSubtype("(null,int)","{any f1}"); }
	@Test public void test_859() { checkNotSubtype("(null,int)","{any f2}"); }
	@Test public void test_860() { checkNotSubtype("(null,int)","{null f1}"); }
	@Test public void test_861() { checkNotSubtype("(null,int)","{null f2}"); }
	@Test public void test_862() { checkNotSubtype("(null,int)","{int f1}"); }
	@Test public void test_863() { checkNotSubtype("(null,int)","{int f2}"); }
	@Test public void test_864() { checkNotSubtype("(null,int)","(any,any)"); }
	@Test public void test_865() { checkNotSubtype("(null,int)","(any,null)"); }
	@Test public void test_866() { checkNotSubtype("(null,int)","(any,int)"); }
	@Test public void test_867() { checkNotSubtype("(null,int)","(null,any)"); }
	@Test public void test_868() { checkNotSubtype("(null,int)","(null,null)"); }
	@Test public void test_869() { checkIsSubtype("(null,int)","(null,int)"); }
	@Test public void test_870() { checkNotSubtype("(null,int)","(int,any)"); }
	@Test public void test_871() { checkNotSubtype("(null,int)","(int,null)"); }
	@Test public void test_872() { checkNotSubtype("(null,int)","(int,int)"); }
	@Test public void test_873() { checkNotSubtype("(null,int)","({any f1},any)"); }
	@Test public void test_874() { checkNotSubtype("(null,int)","({any f2},any)"); }
	@Test public void test_875() { checkNotSubtype("(null,int)","({null f1},null)"); }
	@Test public void test_876() { checkNotSubtype("(null,int)","({null f2},null)"); }
	@Test public void test_877() { checkNotSubtype("(null,int)","({int f1},int)"); }
	@Test public void test_878() { checkNotSubtype("(null,int)","({int f2},int)"); }
	@Test public void test_879() { checkIsSubtype("(null,int)","{{void f1} f1}"); }
	@Test public void test_880() { checkIsSubtype("(null,int)","{{void f2} f1}"); }
	@Test public void test_881() { checkIsSubtype("(null,int)","{{void f1} f2}"); }
	@Test public void test_882() { checkIsSubtype("(null,int)","{{void f2} f2}"); }
	@Test public void test_883() { checkNotSubtype("(null,int)","{{any f1} f1}"); }
	@Test public void test_884() { checkNotSubtype("(null,int)","{{any f2} f1}"); }
	@Test public void test_885() { checkNotSubtype("(null,int)","{{any f1} f2}"); }
	@Test public void test_886() { checkNotSubtype("(null,int)","{{any f2} f2}"); }
	@Test public void test_887() { checkNotSubtype("(null,int)","{{null f1} f1}"); }
	@Test public void test_888() { checkNotSubtype("(null,int)","{{null f2} f1}"); }
	@Test public void test_889() { checkNotSubtype("(null,int)","{{null f1} f2}"); }
	@Test public void test_890() { checkNotSubtype("(null,int)","{{null f2} f2}"); }
	@Test public void test_891() { checkNotSubtype("(null,int)","{{int f1} f1}"); }
	@Test public void test_892() { checkNotSubtype("(null,int)","{{int f2} f1}"); }
	@Test public void test_893() { checkNotSubtype("(null,int)","{{int f1} f2}"); }
	@Test public void test_894() { checkNotSubtype("(null,int)","{{int f2} f2}"); }
	@Test public void test_895() { checkNotSubtype("(null,int)","any"); }
	@Test public void test_896() { checkNotSubtype("(null,int)","null"); }
	@Test public void test_897() { checkNotSubtype("(null,int)","int"); }
	@Test public void test_898() { checkNotSubtype("(null,int)","any"); }
	@Test public void test_899() { checkNotSubtype("(null,int)","any"); }
	@Test public void test_900() { checkNotSubtype("(null,int)","any"); }
	@Test public void test_901() { checkNotSubtype("(null,int)","any"); }
	@Test public void test_902() { checkNotSubtype("(null,int)","null"); }
	@Test public void test_903() { checkNotSubtype("(null,int)","any"); }
	@Test public void test_904() { checkNotSubtype("(null,int)","null"); }
	@Test public void test_905() { checkNotSubtype("(null,int)","null|int"); }
	@Test public void test_906() { checkNotSubtype("(null,int)","int"); }
	@Test public void test_907() { checkNotSubtype("(null,int)","any"); }
	@Test public void test_908() { checkNotSubtype("(null,int)","int|null"); }
	@Test public void test_909() { checkNotSubtype("(null,int)","int"); }
	@Test public void test_910() { checkNotSubtype("(null,int)","any"); }
	@Test public void test_911() { checkNotSubtype("(null,int)","any"); }
	@Test public void test_912() { checkNotSubtype("(null,int)","{null f1}|null"); }
	@Test public void test_913() { checkNotSubtype("(null,int)","{null f2}|null"); }
	@Test public void test_914() { checkNotSubtype("(null,int)","{int f1}|int"); }
	@Test public void test_915() { checkNotSubtype("(null,int)","{int f2}|int"); }
	@Test public void test_916() { checkNotSubtype("(int,any)","any"); }
	@Test public void test_917() { checkNotSubtype("(int,any)","null"); }
	@Test public void test_918() { checkNotSubtype("(int,any)","int"); }
	@Test public void test_919() { checkNotSubtype("(int,any)","{any f1}"); }
	@Test public void test_920() { checkNotSubtype("(int,any)","{any f2}"); }
	@Test public void test_921() { checkNotSubtype("(int,any)","{null f1}"); }
	@Test public void test_922() { checkNotSubtype("(int,any)","{null f2}"); }
	@Test public void test_923() { checkNotSubtype("(int,any)","{int f1}"); }
	@Test public void test_924() { checkNotSubtype("(int,any)","{int f2}"); }
	@Test public void test_925() { checkNotSubtype("(int,any)","(any,any)"); }
	@Test public void test_926() { checkNotSubtype("(int,any)","(any,null)"); }
	@Test public void test_927() { checkNotSubtype("(int,any)","(any,int)"); }
	@Test public void test_928() { checkNotSubtype("(int,any)","(null,any)"); }
	@Test public void test_929() { checkNotSubtype("(int,any)","(null,null)"); }
	@Test public void test_930() { checkNotSubtype("(int,any)","(null,int)"); }
	@Test public void test_931() { checkIsSubtype("(int,any)","(int,any)"); }
	@Test public void test_932() { checkIsSubtype("(int,any)","(int,null)"); }
	@Test public void test_933() { checkIsSubtype("(int,any)","(int,int)"); }
	@Test public void test_934() { checkNotSubtype("(int,any)","({any f1},any)"); }
	@Test public void test_935() { checkNotSubtype("(int,any)","({any f2},any)"); }
	@Test public void test_936() { checkNotSubtype("(int,any)","({null f1},null)"); }
	@Test public void test_937() { checkNotSubtype("(int,any)","({null f2},null)"); }
	@Test public void test_938() { checkNotSubtype("(int,any)","({int f1},int)"); }
	@Test public void test_939() { checkNotSubtype("(int,any)","({int f2},int)"); }
	@Test public void test_940() { checkIsSubtype("(int,any)","{{void f1} f1}"); }
	@Test public void test_941() { checkIsSubtype("(int,any)","{{void f2} f1}"); }
	@Test public void test_942() { checkIsSubtype("(int,any)","{{void f1} f2}"); }
	@Test public void test_943() { checkIsSubtype("(int,any)","{{void f2} f2}"); }
	@Test public void test_944() { checkNotSubtype("(int,any)","{{any f1} f1}"); }
	@Test public void test_945() { checkNotSubtype("(int,any)","{{any f2} f1}"); }
	@Test public void test_946() { checkNotSubtype("(int,any)","{{any f1} f2}"); }
	@Test public void test_947() { checkNotSubtype("(int,any)","{{any f2} f2}"); }
	@Test public void test_948() { checkNotSubtype("(int,any)","{{null f1} f1}"); }
	@Test public void test_949() { checkNotSubtype("(int,any)","{{null f2} f1}"); }
	@Test public void test_950() { checkNotSubtype("(int,any)","{{null f1} f2}"); }
	@Test public void test_951() { checkNotSubtype("(int,any)","{{null f2} f2}"); }
	@Test public void test_952() { checkNotSubtype("(int,any)","{{int f1} f1}"); }
	@Test public void test_953() { checkNotSubtype("(int,any)","{{int f2} f1}"); }
	@Test public void test_954() { checkNotSubtype("(int,any)","{{int f1} f2}"); }
	@Test public void test_955() { checkNotSubtype("(int,any)","{{int f2} f2}"); }
	@Test public void test_956() { checkNotSubtype("(int,any)","any"); }
	@Test public void test_957() { checkNotSubtype("(int,any)","null"); }
	@Test public void test_958() { checkNotSubtype("(int,any)","int"); }
	@Test public void test_959() { checkNotSubtype("(int,any)","any"); }
	@Test public void test_960() { checkNotSubtype("(int,any)","any"); }
	@Test public void test_961() { checkNotSubtype("(int,any)","any"); }
	@Test public void test_962() { checkNotSubtype("(int,any)","any"); }
	@Test public void test_963() { checkNotSubtype("(int,any)","null"); }
	@Test public void test_964() { checkNotSubtype("(int,any)","any"); }
	@Test public void test_965() { checkNotSubtype("(int,any)","null"); }
	@Test public void test_966() { checkNotSubtype("(int,any)","null|int"); }
	@Test public void test_967() { checkNotSubtype("(int,any)","int"); }
	@Test public void test_968() { checkNotSubtype("(int,any)","any"); }
	@Test public void test_969() { checkNotSubtype("(int,any)","int|null"); }
	@Test public void test_970() { checkNotSubtype("(int,any)","int"); }
	@Test public void test_971() { checkNotSubtype("(int,any)","any"); }
	@Test public void test_972() { checkNotSubtype("(int,any)","any"); }
	@Test public void test_973() { checkNotSubtype("(int,any)","{null f1}|null"); }
	@Test public void test_974() { checkNotSubtype("(int,any)","{null f2}|null"); }
	@Test public void test_975() { checkNotSubtype("(int,any)","{int f1}|int"); }
	@Test public void test_976() { checkNotSubtype("(int,any)","{int f2}|int"); }
	@Test public void test_977() { checkNotSubtype("(int,null)","any"); }
	@Test public void test_978() { checkNotSubtype("(int,null)","null"); }
	@Test public void test_979() { checkNotSubtype("(int,null)","int"); }
	@Test public void test_980() { checkNotSubtype("(int,null)","{any f1}"); }
	@Test public void test_981() { checkNotSubtype("(int,null)","{any f2}"); }
	@Test public void test_982() { checkNotSubtype("(int,null)","{null f1}"); }
	@Test public void test_983() { checkNotSubtype("(int,null)","{null f2}"); }
	@Test public void test_984() { checkNotSubtype("(int,null)","{int f1}"); }
	@Test public void test_985() { checkNotSubtype("(int,null)","{int f2}"); }
	@Test public void test_986() { checkNotSubtype("(int,null)","(any,any)"); }
	@Test public void test_987() { checkNotSubtype("(int,null)","(any,null)"); }
	@Test public void test_988() { checkNotSubtype("(int,null)","(any,int)"); }
	@Test public void test_989() { checkNotSubtype("(int,null)","(null,any)"); }
	@Test public void test_990() { checkNotSubtype("(int,null)","(null,null)"); }
	@Test public void test_991() { checkNotSubtype("(int,null)","(null,int)"); }
	@Test public void test_992() { checkNotSubtype("(int,null)","(int,any)"); }
	@Test public void test_993() { checkIsSubtype("(int,null)","(int,null)"); }
	@Test public void test_994() { checkNotSubtype("(int,null)","(int,int)"); }
	@Test public void test_995() { checkNotSubtype("(int,null)","({any f1},any)"); }
	@Test public void test_996() { checkNotSubtype("(int,null)","({any f2},any)"); }
	@Test public void test_997() { checkNotSubtype("(int,null)","({null f1},null)"); }
	@Test public void test_998() { checkNotSubtype("(int,null)","({null f2},null)"); }
	@Test public void test_999() { checkNotSubtype("(int,null)","({int f1},int)"); }
	@Test public void test_1000() { checkNotSubtype("(int,null)","({int f2},int)"); }
	@Test public void test_1001() { checkIsSubtype("(int,null)","{{void f1} f1}"); }
	@Test public void test_1002() { checkIsSubtype("(int,null)","{{void f2} f1}"); }
	@Test public void test_1003() { checkIsSubtype("(int,null)","{{void f1} f2}"); }
	@Test public void test_1004() { checkIsSubtype("(int,null)","{{void f2} f2}"); }
	@Test public void test_1005() { checkNotSubtype("(int,null)","{{any f1} f1}"); }
	@Test public void test_1006() { checkNotSubtype("(int,null)","{{any f2} f1}"); }
	@Test public void test_1007() { checkNotSubtype("(int,null)","{{any f1} f2}"); }
	@Test public void test_1008() { checkNotSubtype("(int,null)","{{any f2} f2}"); }
	@Test public void test_1009() { checkNotSubtype("(int,null)","{{null f1} f1}"); }
	@Test public void test_1010() { checkNotSubtype("(int,null)","{{null f2} f1}"); }
	@Test public void test_1011() { checkNotSubtype("(int,null)","{{null f1} f2}"); }
	@Test public void test_1012() { checkNotSubtype("(int,null)","{{null f2} f2}"); }
	@Test public void test_1013() { checkNotSubtype("(int,null)","{{int f1} f1}"); }
	@Test public void test_1014() { checkNotSubtype("(int,null)","{{int f2} f1}"); }
	@Test public void test_1015() { checkNotSubtype("(int,null)","{{int f1} f2}"); }
	@Test public void test_1016() { checkNotSubtype("(int,null)","{{int f2} f2}"); }
	@Test public void test_1017() { checkNotSubtype("(int,null)","any"); }
	@Test public void test_1018() { checkNotSubtype("(int,null)","null"); }
	@Test public void test_1019() { checkNotSubtype("(int,null)","int"); }
	@Test public void test_1020() { checkNotSubtype("(int,null)","any"); }
	@Test public void test_1021() { checkNotSubtype("(int,null)","any"); }
	@Test public void test_1022() { checkNotSubtype("(int,null)","any"); }
	@Test public void test_1023() { checkNotSubtype("(int,null)","any"); }
	@Test public void test_1024() { checkNotSubtype("(int,null)","null"); }
	@Test public void test_1025() { checkNotSubtype("(int,null)","any"); }
	@Test public void test_1026() { checkNotSubtype("(int,null)","null"); }
	@Test public void test_1027() { checkNotSubtype("(int,null)","null|int"); }
	@Test public void test_1028() { checkNotSubtype("(int,null)","int"); }
	@Test public void test_1029() { checkNotSubtype("(int,null)","any"); }
	@Test public void test_1030() { checkNotSubtype("(int,null)","int|null"); }
	@Test public void test_1031() { checkNotSubtype("(int,null)","int"); }
	@Test public void test_1032() { checkNotSubtype("(int,null)","any"); }
	@Test public void test_1033() { checkNotSubtype("(int,null)","any"); }
	@Test public void test_1034() { checkNotSubtype("(int,null)","{null f1}|null"); }
	@Test public void test_1035() { checkNotSubtype("(int,null)","{null f2}|null"); }
	@Test public void test_1036() { checkNotSubtype("(int,null)","{int f1}|int"); }
	@Test public void test_1037() { checkNotSubtype("(int,null)","{int f2}|int"); }
	@Test public void test_1038() { checkNotSubtype("(int,int)","any"); }
	@Test public void test_1039() { checkNotSubtype("(int,int)","null"); }
	@Test public void test_1040() { checkNotSubtype("(int,int)","int"); }
	@Test public void test_1041() { checkNotSubtype("(int,int)","{any f1}"); }
	@Test public void test_1042() { checkNotSubtype("(int,int)","{any f2}"); }
	@Test public void test_1043() { checkNotSubtype("(int,int)","{null f1}"); }
	@Test public void test_1044() { checkNotSubtype("(int,int)","{null f2}"); }
	@Test public void test_1045() { checkNotSubtype("(int,int)","{int f1}"); }
	@Test public void test_1046() { checkNotSubtype("(int,int)","{int f2}"); }
	@Test public void test_1047() { checkNotSubtype("(int,int)","(any,any)"); }
	@Test public void test_1048() { checkNotSubtype("(int,int)","(any,null)"); }
	@Test public void test_1049() { checkNotSubtype("(int,int)","(any,int)"); }
	@Test public void test_1050() { checkNotSubtype("(int,int)","(null,any)"); }
	@Test public void test_1051() { checkNotSubtype("(int,int)","(null,null)"); }
	@Test public void test_1052() { checkNotSubtype("(int,int)","(null,int)"); }
	@Test public void test_1053() { checkNotSubtype("(int,int)","(int,any)"); }
	@Test public void test_1054() { checkNotSubtype("(int,int)","(int,null)"); }
	@Test public void test_1055() { checkIsSubtype("(int,int)","(int,int)"); }
	@Test public void test_1056() { checkNotSubtype("(int,int)","({any f1},any)"); }
	@Test public void test_1057() { checkNotSubtype("(int,int)","({any f2},any)"); }
	@Test public void test_1058() { checkNotSubtype("(int,int)","({null f1},null)"); }
	@Test public void test_1059() { checkNotSubtype("(int,int)","({null f2},null)"); }
	@Test public void test_1060() { checkNotSubtype("(int,int)","({int f1},int)"); }
	@Test public void test_1061() { checkNotSubtype("(int,int)","({int f2},int)"); }
	@Test public void test_1062() { checkIsSubtype("(int,int)","{{void f1} f1}"); }
	@Test public void test_1063() { checkIsSubtype("(int,int)","{{void f2} f1}"); }
	@Test public void test_1064() { checkIsSubtype("(int,int)","{{void f1} f2}"); }
	@Test public void test_1065() { checkIsSubtype("(int,int)","{{void f2} f2}"); }
	@Test public void test_1066() { checkNotSubtype("(int,int)","{{any f1} f1}"); }
	@Test public void test_1067() { checkNotSubtype("(int,int)","{{any f2} f1}"); }
	@Test public void test_1068() { checkNotSubtype("(int,int)","{{any f1} f2}"); }
	@Test public void test_1069() { checkNotSubtype("(int,int)","{{any f2} f2}"); }
	@Test public void test_1070() { checkNotSubtype("(int,int)","{{null f1} f1}"); }
	@Test public void test_1071() { checkNotSubtype("(int,int)","{{null f2} f1}"); }
	@Test public void test_1072() { checkNotSubtype("(int,int)","{{null f1} f2}"); }
	@Test public void test_1073() { checkNotSubtype("(int,int)","{{null f2} f2}"); }
	@Test public void test_1074() { checkNotSubtype("(int,int)","{{int f1} f1}"); }
	@Test public void test_1075() { checkNotSubtype("(int,int)","{{int f2} f1}"); }
	@Test public void test_1076() { checkNotSubtype("(int,int)","{{int f1} f2}"); }
	@Test public void test_1077() { checkNotSubtype("(int,int)","{{int f2} f2}"); }
	@Test public void test_1078() { checkNotSubtype("(int,int)","any"); }
	@Test public void test_1079() { checkNotSubtype("(int,int)","null"); }
	@Test public void test_1080() { checkNotSubtype("(int,int)","int"); }
	@Test public void test_1081() { checkNotSubtype("(int,int)","any"); }
	@Test public void test_1082() { checkNotSubtype("(int,int)","any"); }
	@Test public void test_1083() { checkNotSubtype("(int,int)","any"); }
	@Test public void test_1084() { checkNotSubtype("(int,int)","any"); }
	@Test public void test_1085() { checkNotSubtype("(int,int)","null"); }
	@Test public void test_1086() { checkNotSubtype("(int,int)","any"); }
	@Test public void test_1087() { checkNotSubtype("(int,int)","null"); }
	@Test public void test_1088() { checkNotSubtype("(int,int)","null|int"); }
	@Test public void test_1089() { checkNotSubtype("(int,int)","int"); }
	@Test public void test_1090() { checkNotSubtype("(int,int)","any"); }
	@Test public void test_1091() { checkNotSubtype("(int,int)","int|null"); }
	@Test public void test_1092() { checkNotSubtype("(int,int)","int"); }
	@Test public void test_1093() { checkNotSubtype("(int,int)","any"); }
	@Test public void test_1094() { checkNotSubtype("(int,int)","any"); }
	@Test public void test_1095() { checkNotSubtype("(int,int)","{null f1}|null"); }
	@Test public void test_1096() { checkNotSubtype("(int,int)","{null f2}|null"); }
	@Test public void test_1097() { checkNotSubtype("(int,int)","{int f1}|int"); }
	@Test public void test_1098() { checkNotSubtype("(int,int)","{int f2}|int"); }
	@Test public void test_1099() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1100() { checkNotSubtype("({any f1},any)","null"); }
	@Test public void test_1101() { checkNotSubtype("({any f1},any)","int"); }
	@Test public void test_1102() { checkNotSubtype("({any f1},any)","{any f1}"); }
	@Test public void test_1103() { checkNotSubtype("({any f1},any)","{any f2}"); }
	@Test public void test_1104() { checkNotSubtype("({any f1},any)","{null f1}"); }
	@Test public void test_1105() { checkNotSubtype("({any f1},any)","{null f2}"); }
	@Test public void test_1106() { checkNotSubtype("({any f1},any)","{int f1}"); }
	@Test public void test_1107() { checkNotSubtype("({any f1},any)","{int f2}"); }
	@Test public void test_1108() { checkNotSubtype("({any f1},any)","(any,any)"); }
	@Test public void test_1109() { checkNotSubtype("({any f1},any)","(any,null)"); }
	@Test public void test_1110() { checkNotSubtype("({any f1},any)","(any,int)"); }
	@Test public void test_1111() { checkNotSubtype("({any f1},any)","(null,any)"); }
	@Test public void test_1112() { checkNotSubtype("({any f1},any)","(null,null)"); }
	@Test public void test_1113() { checkNotSubtype("({any f1},any)","(null,int)"); }
	@Test public void test_1114() { checkNotSubtype("({any f1},any)","(int,any)"); }
	@Test public void test_1115() { checkNotSubtype("({any f1},any)","(int,null)"); }
	@Test public void test_1116() { checkNotSubtype("({any f1},any)","(int,int)"); }
	@Test public void test_1117() { checkIsSubtype("({any f1},any)","({any f1},any)"); }
	@Test public void test_1118() { checkNotSubtype("({any f1},any)","({any f2},any)"); }
	@Test public void test_1119() { checkIsSubtype("({any f1},any)","({null f1},null)"); }
	@Test public void test_1120() { checkNotSubtype("({any f1},any)","({null f2},null)"); }
	@Test public void test_1121() { checkIsSubtype("({any f1},any)","({int f1},int)"); }
	@Test public void test_1122() { checkNotSubtype("({any f1},any)","({int f2},int)"); }
	@Test public void test_1123() { checkIsSubtype("({any f1},any)","{{void f1} f1}"); }
	@Test public void test_1124() { checkIsSubtype("({any f1},any)","{{void f2} f1}"); }
	@Test public void test_1125() { checkIsSubtype("({any f1},any)","{{void f1} f2}"); }
	@Test public void test_1126() { checkIsSubtype("({any f1},any)","{{void f2} f2}"); }
	@Test public void test_1127() { checkNotSubtype("({any f1},any)","{{any f1} f1}"); }
	@Test public void test_1128() { checkNotSubtype("({any f1},any)","{{any f2} f1}"); }
	@Test public void test_1129() { checkNotSubtype("({any f1},any)","{{any f1} f2}"); }
	@Test public void test_1130() { checkNotSubtype("({any f1},any)","{{any f2} f2}"); }
	@Test public void test_1131() { checkNotSubtype("({any f1},any)","{{null f1} f1}"); }
	@Test public void test_1132() { checkNotSubtype("({any f1},any)","{{null f2} f1}"); }
	@Test public void test_1133() { checkNotSubtype("({any f1},any)","{{null f1} f2}"); }
	@Test public void test_1134() { checkNotSubtype("({any f1},any)","{{null f2} f2}"); }
	@Test public void test_1135() { checkNotSubtype("({any f1},any)","{{int f1} f1}"); }
	@Test public void test_1136() { checkNotSubtype("({any f1},any)","{{int f2} f1}"); }
	@Test public void test_1137() { checkNotSubtype("({any f1},any)","{{int f1} f2}"); }
	@Test public void test_1138() { checkNotSubtype("({any f1},any)","{{int f2} f2}"); }
	@Test public void test_1139() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1140() { checkNotSubtype("({any f1},any)","null"); }
	@Test public void test_1141() { checkNotSubtype("({any f1},any)","int"); }
	@Test public void test_1142() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1143() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1144() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1145() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1146() { checkNotSubtype("({any f1},any)","null"); }
	@Test public void test_1147() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1148() { checkNotSubtype("({any f1},any)","null"); }
	@Test public void test_1149() { checkNotSubtype("({any f1},any)","null|int"); }
	@Test public void test_1150() { checkNotSubtype("({any f1},any)","int"); }
	@Test public void test_1151() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1152() { checkNotSubtype("({any f1},any)","int|null"); }
	@Test public void test_1153() { checkNotSubtype("({any f1},any)","int"); }
	@Test public void test_1154() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1155() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1156() { checkNotSubtype("({any f1},any)","{null f1}|null"); }
	@Test public void test_1157() { checkNotSubtype("({any f1},any)","{null f2}|null"); }
	@Test public void test_1158() { checkNotSubtype("({any f1},any)","{int f1}|int"); }
	@Test public void test_1159() { checkNotSubtype("({any f1},any)","{int f2}|int"); }
	@Test public void test_1160() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1161() { checkNotSubtype("({any f2},any)","null"); }
	@Test public void test_1162() { checkNotSubtype("({any f2},any)","int"); }
	@Test public void test_1163() { checkNotSubtype("({any f2},any)","{any f1}"); }
	@Test public void test_1164() { checkNotSubtype("({any f2},any)","{any f2}"); }
	@Test public void test_1165() { checkNotSubtype("({any f2},any)","{null f1}"); }
	@Test public void test_1166() { checkNotSubtype("({any f2},any)","{null f2}"); }
	@Test public void test_1167() { checkNotSubtype("({any f2},any)","{int f1}"); }
	@Test public void test_1168() { checkNotSubtype("({any f2},any)","{int f2}"); }
	@Test public void test_1169() { checkNotSubtype("({any f2},any)","(any,any)"); }
	@Test public void test_1170() { checkNotSubtype("({any f2},any)","(any,null)"); }
	@Test public void test_1171() { checkNotSubtype("({any f2},any)","(any,int)"); }
	@Test public void test_1172() { checkNotSubtype("({any f2},any)","(null,any)"); }
	@Test public void test_1173() { checkNotSubtype("({any f2},any)","(null,null)"); }
	@Test public void test_1174() { checkNotSubtype("({any f2},any)","(null,int)"); }
	@Test public void test_1175() { checkNotSubtype("({any f2},any)","(int,any)"); }
	@Test public void test_1176() { checkNotSubtype("({any f2},any)","(int,null)"); }
	@Test public void test_1177() { checkNotSubtype("({any f2},any)","(int,int)"); }
	@Test public void test_1178() { checkNotSubtype("({any f2},any)","({any f1},any)"); }
	@Test public void test_1179() { checkIsSubtype("({any f2},any)","({any f2},any)"); }
	@Test public void test_1180() { checkNotSubtype("({any f2},any)","({null f1},null)"); }
	@Test public void test_1181() { checkIsSubtype("({any f2},any)","({null f2},null)"); }
	@Test public void test_1182() { checkNotSubtype("({any f2},any)","({int f1},int)"); }
	@Test public void test_1183() { checkIsSubtype("({any f2},any)","({int f2},int)"); }
	@Test public void test_1184() { checkIsSubtype("({any f2},any)","{{void f1} f1}"); }
	@Test public void test_1185() { checkIsSubtype("({any f2},any)","{{void f2} f1}"); }
	@Test public void test_1186() { checkIsSubtype("({any f2},any)","{{void f1} f2}"); }
	@Test public void test_1187() { checkIsSubtype("({any f2},any)","{{void f2} f2}"); }
	@Test public void test_1188() { checkNotSubtype("({any f2},any)","{{any f1} f1}"); }
	@Test public void test_1189() { checkNotSubtype("({any f2},any)","{{any f2} f1}"); }
	@Test public void test_1190() { checkNotSubtype("({any f2},any)","{{any f1} f2}"); }
	@Test public void test_1191() { checkNotSubtype("({any f2},any)","{{any f2} f2}"); }
	@Test public void test_1192() { checkNotSubtype("({any f2},any)","{{null f1} f1}"); }
	@Test public void test_1193() { checkNotSubtype("({any f2},any)","{{null f2} f1}"); }
	@Test public void test_1194() { checkNotSubtype("({any f2},any)","{{null f1} f2}"); }
	@Test public void test_1195() { checkNotSubtype("({any f2},any)","{{null f2} f2}"); }
	@Test public void test_1196() { checkNotSubtype("({any f2},any)","{{int f1} f1}"); }
	@Test public void test_1197() { checkNotSubtype("({any f2},any)","{{int f2} f1}"); }
	@Test public void test_1198() { checkNotSubtype("({any f2},any)","{{int f1} f2}"); }
	@Test public void test_1199() { checkNotSubtype("({any f2},any)","{{int f2} f2}"); }
	@Test public void test_1200() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1201() { checkNotSubtype("({any f2},any)","null"); }
	@Test public void test_1202() { checkNotSubtype("({any f2},any)","int"); }
	@Test public void test_1203() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1204() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1205() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1206() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1207() { checkNotSubtype("({any f2},any)","null"); }
	@Test public void test_1208() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1209() { checkNotSubtype("({any f2},any)","null"); }
	@Test public void test_1210() { checkNotSubtype("({any f2},any)","null|int"); }
	@Test public void test_1211() { checkNotSubtype("({any f2},any)","int"); }
	@Test public void test_1212() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1213() { checkNotSubtype("({any f2},any)","int|null"); }
	@Test public void test_1214() { checkNotSubtype("({any f2},any)","int"); }
	@Test public void test_1215() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1216() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1217() { checkNotSubtype("({any f2},any)","{null f1}|null"); }
	@Test public void test_1218() { checkNotSubtype("({any f2},any)","{null f2}|null"); }
	@Test public void test_1219() { checkNotSubtype("({any f2},any)","{int f1}|int"); }
	@Test public void test_1220() { checkNotSubtype("({any f2},any)","{int f2}|int"); }
	@Test public void test_1221() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1222() { checkNotSubtype("({null f1},null)","null"); }
	@Test public void test_1223() { checkNotSubtype("({null f1},null)","int"); }
	@Test public void test_1224() { checkNotSubtype("({null f1},null)","{any f1}"); }
	@Test public void test_1225() { checkNotSubtype("({null f1},null)","{any f2}"); }
	@Test public void test_1226() { checkNotSubtype("({null f1},null)","{null f1}"); }
	@Test public void test_1227() { checkNotSubtype("({null f1},null)","{null f2}"); }
	@Test public void test_1228() { checkNotSubtype("({null f1},null)","{int f1}"); }
	@Test public void test_1229() { checkNotSubtype("({null f1},null)","{int f2}"); }
	@Test public void test_1230() { checkNotSubtype("({null f1},null)","(any,any)"); }
	@Test public void test_1231() { checkNotSubtype("({null f1},null)","(any,null)"); }
	@Test public void test_1232() { checkNotSubtype("({null f1},null)","(any,int)"); }
	@Test public void test_1233() { checkNotSubtype("({null f1},null)","(null,any)"); }
	@Test public void test_1234() { checkNotSubtype("({null f1},null)","(null,null)"); }
	@Test public void test_1235() { checkNotSubtype("({null f1},null)","(null,int)"); }
	@Test public void test_1236() { checkNotSubtype("({null f1},null)","(int,any)"); }
	@Test public void test_1237() { checkNotSubtype("({null f1},null)","(int,null)"); }
	@Test public void test_1238() { checkNotSubtype("({null f1},null)","(int,int)"); }
	@Test public void test_1239() { checkNotSubtype("({null f1},null)","({any f1},any)"); }
	@Test public void test_1240() { checkNotSubtype("({null f1},null)","({any f2},any)"); }
	@Test public void test_1241() { checkIsSubtype("({null f1},null)","({null f1},null)"); }
	@Test public void test_1242() { checkNotSubtype("({null f1},null)","({null f2},null)"); }
	@Test public void test_1243() { checkNotSubtype("({null f1},null)","({int f1},int)"); }
	@Test public void test_1244() { checkNotSubtype("({null f1},null)","({int f2},int)"); }
	@Test public void test_1245() { checkIsSubtype("({null f1},null)","{{void f1} f1}"); }
	@Test public void test_1246() { checkIsSubtype("({null f1},null)","{{void f2} f1}"); }
	@Test public void test_1247() { checkIsSubtype("({null f1},null)","{{void f1} f2}"); }
	@Test public void test_1248() { checkIsSubtype("({null f1},null)","{{void f2} f2}"); }
	@Test public void test_1249() { checkNotSubtype("({null f1},null)","{{any f1} f1}"); }
	@Test public void test_1250() { checkNotSubtype("({null f1},null)","{{any f2} f1}"); }
	@Test public void test_1251() { checkNotSubtype("({null f1},null)","{{any f1} f2}"); }
	@Test public void test_1252() { checkNotSubtype("({null f1},null)","{{any f2} f2}"); }
	@Test public void test_1253() { checkNotSubtype("({null f1},null)","{{null f1} f1}"); }
	@Test public void test_1254() { checkNotSubtype("({null f1},null)","{{null f2} f1}"); }
	@Test public void test_1255() { checkNotSubtype("({null f1},null)","{{null f1} f2}"); }
	@Test public void test_1256() { checkNotSubtype("({null f1},null)","{{null f2} f2}"); }
	@Test public void test_1257() { checkNotSubtype("({null f1},null)","{{int f1} f1}"); }
	@Test public void test_1258() { checkNotSubtype("({null f1},null)","{{int f2} f1}"); }
	@Test public void test_1259() { checkNotSubtype("({null f1},null)","{{int f1} f2}"); }
	@Test public void test_1260() { checkNotSubtype("({null f1},null)","{{int f2} f2}"); }
	@Test public void test_1261() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1262() { checkNotSubtype("({null f1},null)","null"); }
	@Test public void test_1263() { checkNotSubtype("({null f1},null)","int"); }
	@Test public void test_1264() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1265() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1266() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1267() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1268() { checkNotSubtype("({null f1},null)","null"); }
	@Test public void test_1269() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1270() { checkNotSubtype("({null f1},null)","null"); }
	@Test public void test_1271() { checkNotSubtype("({null f1},null)","null|int"); }
	@Test public void test_1272() { checkNotSubtype("({null f1},null)","int"); }
	@Test public void test_1273() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1274() { checkNotSubtype("({null f1},null)","int|null"); }
	@Test public void test_1275() { checkNotSubtype("({null f1},null)","int"); }
	@Test public void test_1276() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1277() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1278() { checkNotSubtype("({null f1},null)","{null f1}|null"); }
	@Test public void test_1279() { checkNotSubtype("({null f1},null)","{null f2}|null"); }
	@Test public void test_1280() { checkNotSubtype("({null f1},null)","{int f1}|int"); }
	@Test public void test_1281() { checkNotSubtype("({null f1},null)","{int f2}|int"); }
	@Test public void test_1282() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1283() { checkNotSubtype("({null f2},null)","null"); }
	@Test public void test_1284() { checkNotSubtype("({null f2},null)","int"); }
	@Test public void test_1285() { checkNotSubtype("({null f2},null)","{any f1}"); }
	@Test public void test_1286() { checkNotSubtype("({null f2},null)","{any f2}"); }
	@Test public void test_1287() { checkNotSubtype("({null f2},null)","{null f1}"); }
	@Test public void test_1288() { checkNotSubtype("({null f2},null)","{null f2}"); }
	@Test public void test_1289() { checkNotSubtype("({null f2},null)","{int f1}"); }
	@Test public void test_1290() { checkNotSubtype("({null f2},null)","{int f2}"); }
	@Test public void test_1291() { checkNotSubtype("({null f2},null)","(any,any)"); }
	@Test public void test_1292() { checkNotSubtype("({null f2},null)","(any,null)"); }
	@Test public void test_1293() { checkNotSubtype("({null f2},null)","(any,int)"); }
	@Test public void test_1294() { checkNotSubtype("({null f2},null)","(null,any)"); }
	@Test public void test_1295() { checkNotSubtype("({null f2},null)","(null,null)"); }
	@Test public void test_1296() { checkNotSubtype("({null f2},null)","(null,int)"); }
	@Test public void test_1297() { checkNotSubtype("({null f2},null)","(int,any)"); }
	@Test public void test_1298() { checkNotSubtype("({null f2},null)","(int,null)"); }
	@Test public void test_1299() { checkNotSubtype("({null f2},null)","(int,int)"); }
	@Test public void test_1300() { checkNotSubtype("({null f2},null)","({any f1},any)"); }
	@Test public void test_1301() { checkNotSubtype("({null f2},null)","({any f2},any)"); }
	@Test public void test_1302() { checkNotSubtype("({null f2},null)","({null f1},null)"); }
	@Test public void test_1303() { checkIsSubtype("({null f2},null)","({null f2},null)"); }
	@Test public void test_1304() { checkNotSubtype("({null f2},null)","({int f1},int)"); }
	@Test public void test_1305() { checkNotSubtype("({null f2},null)","({int f2},int)"); }
	@Test public void test_1306() { checkIsSubtype("({null f2},null)","{{void f1} f1}"); }
	@Test public void test_1307() { checkIsSubtype("({null f2},null)","{{void f2} f1}"); }
	@Test public void test_1308() { checkIsSubtype("({null f2},null)","{{void f1} f2}"); }
	@Test public void test_1309() { checkIsSubtype("({null f2},null)","{{void f2} f2}"); }
	@Test public void test_1310() { checkNotSubtype("({null f2},null)","{{any f1} f1}"); }
	@Test public void test_1311() { checkNotSubtype("({null f2},null)","{{any f2} f1}"); }
	@Test public void test_1312() { checkNotSubtype("({null f2},null)","{{any f1} f2}"); }
	@Test public void test_1313() { checkNotSubtype("({null f2},null)","{{any f2} f2}"); }
	@Test public void test_1314() { checkNotSubtype("({null f2},null)","{{null f1} f1}"); }
	@Test public void test_1315() { checkNotSubtype("({null f2},null)","{{null f2} f1}"); }
	@Test public void test_1316() { checkNotSubtype("({null f2},null)","{{null f1} f2}"); }
	@Test public void test_1317() { checkNotSubtype("({null f2},null)","{{null f2} f2}"); }
	@Test public void test_1318() { checkNotSubtype("({null f2},null)","{{int f1} f1}"); }
	@Test public void test_1319() { checkNotSubtype("({null f2},null)","{{int f2} f1}"); }
	@Test public void test_1320() { checkNotSubtype("({null f2},null)","{{int f1} f2}"); }
	@Test public void test_1321() { checkNotSubtype("({null f2},null)","{{int f2} f2}"); }
	@Test public void test_1322() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1323() { checkNotSubtype("({null f2},null)","null"); }
	@Test public void test_1324() { checkNotSubtype("({null f2},null)","int"); }
	@Test public void test_1325() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1326() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1327() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1328() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1329() { checkNotSubtype("({null f2},null)","null"); }
	@Test public void test_1330() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1331() { checkNotSubtype("({null f2},null)","null"); }
	@Test public void test_1332() { checkNotSubtype("({null f2},null)","null|int"); }
	@Test public void test_1333() { checkNotSubtype("({null f2},null)","int"); }
	@Test public void test_1334() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1335() { checkNotSubtype("({null f2},null)","int|null"); }
	@Test public void test_1336() { checkNotSubtype("({null f2},null)","int"); }
	@Test public void test_1337() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1338() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1339() { checkNotSubtype("({null f2},null)","{null f1}|null"); }
	@Test public void test_1340() { checkNotSubtype("({null f2},null)","{null f2}|null"); }
	@Test public void test_1341() { checkNotSubtype("({null f2},null)","{int f1}|int"); }
	@Test public void test_1342() { checkNotSubtype("({null f2},null)","{int f2}|int"); }
	@Test public void test_1343() { checkNotSubtype("({int f1},int)","any"); }
	@Test public void test_1344() { checkNotSubtype("({int f1},int)","null"); }
	@Test public void test_1345() { checkNotSubtype("({int f1},int)","int"); }
	@Test public void test_1346() { checkNotSubtype("({int f1},int)","{any f1}"); }
	@Test public void test_1347() { checkNotSubtype("({int f1},int)","{any f2}"); }
	@Test public void test_1348() { checkNotSubtype("({int f1},int)","{null f1}"); }
	@Test public void test_1349() { checkNotSubtype("({int f1},int)","{null f2}"); }
	@Test public void test_1350() { checkNotSubtype("({int f1},int)","{int f1}"); }
	@Test public void test_1351() { checkNotSubtype("({int f1},int)","{int f2}"); }
	@Test public void test_1352() { checkNotSubtype("({int f1},int)","(any,any)"); }
	@Test public void test_1353() { checkNotSubtype("({int f1},int)","(any,null)"); }
	@Test public void test_1354() { checkNotSubtype("({int f1},int)","(any,int)"); }
	@Test public void test_1355() { checkNotSubtype("({int f1},int)","(null,any)"); }
	@Test public void test_1356() { checkNotSubtype("({int f1},int)","(null,null)"); }
	@Test public void test_1357() { checkNotSubtype("({int f1},int)","(null,int)"); }
	@Test public void test_1358() { checkNotSubtype("({int f1},int)","(int,any)"); }
	@Test public void test_1359() { checkNotSubtype("({int f1},int)","(int,null)"); }
	@Test public void test_1360() { checkNotSubtype("({int f1},int)","(int,int)"); }
	@Test public void test_1361() { checkNotSubtype("({int f1},int)","({any f1},any)"); }
	@Test public void test_1362() { checkNotSubtype("({int f1},int)","({any f2},any)"); }
	@Test public void test_1363() { checkNotSubtype("({int f1},int)","({null f1},null)"); }
	@Test public void test_1364() { checkNotSubtype("({int f1},int)","({null f2},null)"); }
	@Test public void test_1365() { checkIsSubtype("({int f1},int)","({int f1},int)"); }
	@Test public void test_1366() { checkNotSubtype("({int f1},int)","({int f2},int)"); }
	@Test public void test_1367() { checkIsSubtype("({int f1},int)","{{void f1} f1}"); }
	@Test public void test_1368() { checkIsSubtype("({int f1},int)","{{void f2} f1}"); }
	@Test public void test_1369() { checkIsSubtype("({int f1},int)","{{void f1} f2}"); }
	@Test public void test_1370() { checkIsSubtype("({int f1},int)","{{void f2} f2}"); }
	@Test public void test_1371() { checkNotSubtype("({int f1},int)","{{any f1} f1}"); }
	@Test public void test_1372() { checkNotSubtype("({int f1},int)","{{any f2} f1}"); }
	@Test public void test_1373() { checkNotSubtype("({int f1},int)","{{any f1} f2}"); }
	@Test public void test_1374() { checkNotSubtype("({int f1},int)","{{any f2} f2}"); }
	@Test public void test_1375() { checkNotSubtype("({int f1},int)","{{null f1} f1}"); }
	@Test public void test_1376() { checkNotSubtype("({int f1},int)","{{null f2} f1}"); }
	@Test public void test_1377() { checkNotSubtype("({int f1},int)","{{null f1} f2}"); }
	@Test public void test_1378() { checkNotSubtype("({int f1},int)","{{null f2} f2}"); }
	@Test public void test_1379() { checkNotSubtype("({int f1},int)","{{int f1} f1}"); }
	@Test public void test_1380() { checkNotSubtype("({int f1},int)","{{int f2} f1}"); }
	@Test public void test_1381() { checkNotSubtype("({int f1},int)","{{int f1} f2}"); }
	@Test public void test_1382() { checkNotSubtype("({int f1},int)","{{int f2} f2}"); }
	@Test public void test_1383() { checkNotSubtype("({int f1},int)","any"); }
	@Test public void test_1384() { checkNotSubtype("({int f1},int)","null"); }
	@Test public void test_1385() { checkNotSubtype("({int f1},int)","int"); }
	@Test public void test_1386() { checkNotSubtype("({int f1},int)","any"); }
	@Test public void test_1387() { checkNotSubtype("({int f1},int)","any"); }
	@Test public void test_1388() { checkNotSubtype("({int f1},int)","any"); }
	@Test public void test_1389() { checkNotSubtype("({int f1},int)","any"); }
	@Test public void test_1390() { checkNotSubtype("({int f1},int)","null"); }
	@Test public void test_1391() { checkNotSubtype("({int f1},int)","any"); }
	@Test public void test_1392() { checkNotSubtype("({int f1},int)","null"); }
	@Test public void test_1393() { checkNotSubtype("({int f1},int)","null|int"); }
	@Test public void test_1394() { checkNotSubtype("({int f1},int)","int"); }
	@Test public void test_1395() { checkNotSubtype("({int f1},int)","any"); }
	@Test public void test_1396() { checkNotSubtype("({int f1},int)","int|null"); }
	@Test public void test_1397() { checkNotSubtype("({int f1},int)","int"); }
	@Test public void test_1398() { checkNotSubtype("({int f1},int)","any"); }
	@Test public void test_1399() { checkNotSubtype("({int f1},int)","any"); }
	@Test public void test_1400() { checkNotSubtype("({int f1},int)","{null f1}|null"); }
	@Test public void test_1401() { checkNotSubtype("({int f1},int)","{null f2}|null"); }
	@Test public void test_1402() { checkNotSubtype("({int f1},int)","{int f1}|int"); }
	@Test public void test_1403() { checkNotSubtype("({int f1},int)","{int f2}|int"); }
	@Test public void test_1404() { checkNotSubtype("({int f2},int)","any"); }
	@Test public void test_1405() { checkNotSubtype("({int f2},int)","null"); }
	@Test public void test_1406() { checkNotSubtype("({int f2},int)","int"); }
	@Test public void test_1407() { checkNotSubtype("({int f2},int)","{any f1}"); }
	@Test public void test_1408() { checkNotSubtype("({int f2},int)","{any f2}"); }
	@Test public void test_1409() { checkNotSubtype("({int f2},int)","{null f1}"); }
	@Test public void test_1410() { checkNotSubtype("({int f2},int)","{null f2}"); }
	@Test public void test_1411() { checkNotSubtype("({int f2},int)","{int f1}"); }
	@Test public void test_1412() { checkNotSubtype("({int f2},int)","{int f2}"); }
	@Test public void test_1413() { checkNotSubtype("({int f2},int)","(any,any)"); }
	@Test public void test_1414() { checkNotSubtype("({int f2},int)","(any,null)"); }
	@Test public void test_1415() { checkNotSubtype("({int f2},int)","(any,int)"); }
	@Test public void test_1416() { checkNotSubtype("({int f2},int)","(null,any)"); }
	@Test public void test_1417() { checkNotSubtype("({int f2},int)","(null,null)"); }
	@Test public void test_1418() { checkNotSubtype("({int f2},int)","(null,int)"); }
	@Test public void test_1419() { checkNotSubtype("({int f2},int)","(int,any)"); }
	@Test public void test_1420() { checkNotSubtype("({int f2},int)","(int,null)"); }
	@Test public void test_1421() { checkNotSubtype("({int f2},int)","(int,int)"); }
	@Test public void test_1422() { checkNotSubtype("({int f2},int)","({any f1},any)"); }
	@Test public void test_1423() { checkNotSubtype("({int f2},int)","({any f2},any)"); }
	@Test public void test_1424() { checkNotSubtype("({int f2},int)","({null f1},null)"); }
	@Test public void test_1425() { checkNotSubtype("({int f2},int)","({null f2},null)"); }
	@Test public void test_1426() { checkNotSubtype("({int f2},int)","({int f1},int)"); }
	@Test public void test_1427() { checkIsSubtype("({int f2},int)","({int f2},int)"); }
	@Test public void test_1428() { checkIsSubtype("({int f2},int)","{{void f1} f1}"); }
	@Test public void test_1429() { checkIsSubtype("({int f2},int)","{{void f2} f1}"); }
	@Test public void test_1430() { checkIsSubtype("({int f2},int)","{{void f1} f2}"); }
	@Test public void test_1431() { checkIsSubtype("({int f2},int)","{{void f2} f2}"); }
	@Test public void test_1432() { checkNotSubtype("({int f2},int)","{{any f1} f1}"); }
	@Test public void test_1433() { checkNotSubtype("({int f2},int)","{{any f2} f1}"); }
	@Test public void test_1434() { checkNotSubtype("({int f2},int)","{{any f1} f2}"); }
	@Test public void test_1435() { checkNotSubtype("({int f2},int)","{{any f2} f2}"); }
	@Test public void test_1436() { checkNotSubtype("({int f2},int)","{{null f1} f1}"); }
	@Test public void test_1437() { checkNotSubtype("({int f2},int)","{{null f2} f1}"); }
	@Test public void test_1438() { checkNotSubtype("({int f2},int)","{{null f1} f2}"); }
	@Test public void test_1439() { checkNotSubtype("({int f2},int)","{{null f2} f2}"); }
	@Test public void test_1440() { checkNotSubtype("({int f2},int)","{{int f1} f1}"); }
	@Test public void test_1441() { checkNotSubtype("({int f2},int)","{{int f2} f1}"); }
	@Test public void test_1442() { checkNotSubtype("({int f2},int)","{{int f1} f2}"); }
	@Test public void test_1443() { checkNotSubtype("({int f2},int)","{{int f2} f2}"); }
	@Test public void test_1444() { checkNotSubtype("({int f2},int)","any"); }
	@Test public void test_1445() { checkNotSubtype("({int f2},int)","null"); }
	@Test public void test_1446() { checkNotSubtype("({int f2},int)","int"); }
	@Test public void test_1447() { checkNotSubtype("({int f2},int)","any"); }
	@Test public void test_1448() { checkNotSubtype("({int f2},int)","any"); }
	@Test public void test_1449() { checkNotSubtype("({int f2},int)","any"); }
	@Test public void test_1450() { checkNotSubtype("({int f2},int)","any"); }
	@Test public void test_1451() { checkNotSubtype("({int f2},int)","null"); }
	@Test public void test_1452() { checkNotSubtype("({int f2},int)","any"); }
	@Test public void test_1453() { checkNotSubtype("({int f2},int)","null"); }
	@Test public void test_1454() { checkNotSubtype("({int f2},int)","null|int"); }
	@Test public void test_1455() { checkNotSubtype("({int f2},int)","int"); }
	@Test public void test_1456() { checkNotSubtype("({int f2},int)","any"); }
	@Test public void test_1457() { checkNotSubtype("({int f2},int)","int|null"); }
	@Test public void test_1458() { checkNotSubtype("({int f2},int)","int"); }
	@Test public void test_1459() { checkNotSubtype("({int f2},int)","any"); }
	@Test public void test_1460() { checkNotSubtype("({int f2},int)","any"); }
	@Test public void test_1461() { checkNotSubtype("({int f2},int)","{null f1}|null"); }
	@Test public void test_1462() { checkNotSubtype("({int f2},int)","{null f2}|null"); }
	@Test public void test_1463() { checkNotSubtype("({int f2},int)","{int f1}|int"); }
	@Test public void test_1464() { checkNotSubtype("({int f2},int)","{int f2}|int"); }
	@Test public void test_1465() { checkNotSubtype("{{void f1} f1}","any"); }
	@Test public void test_1466() { checkNotSubtype("{{void f1} f1}","null"); }
	@Test public void test_1467() { checkNotSubtype("{{void f1} f1}","int"); }
	@Test public void test_1468() { checkNotSubtype("{{void f1} f1}","{any f1}"); }
	@Test public void test_1469() { checkNotSubtype("{{void f1} f1}","{any f2}"); }
	@Test public void test_1470() { checkNotSubtype("{{void f1} f1}","{null f1}"); }
	@Test public void test_1471() { checkNotSubtype("{{void f1} f1}","{null f2}"); }
	@Test public void test_1472() { checkNotSubtype("{{void f1} f1}","{int f1}"); }
	@Test public void test_1473() { checkNotSubtype("{{void f1} f1}","{int f2}"); }
	@Test public void test_1474() { checkNotSubtype("{{void f1} f1}","(any,any)"); }
	@Test public void test_1475() { checkNotSubtype("{{void f1} f1}","(any,null)"); }
	@Test public void test_1476() { checkNotSubtype("{{void f1} f1}","(any,int)"); }
	@Test public void test_1477() { checkNotSubtype("{{void f1} f1}","(null,any)"); }
	@Test public void test_1478() { checkNotSubtype("{{void f1} f1}","(null,null)"); }
	@Test public void test_1479() { checkNotSubtype("{{void f1} f1}","(null,int)"); }
	@Test public void test_1480() { checkNotSubtype("{{void f1} f1}","(int,any)"); }
	@Test public void test_1481() { checkNotSubtype("{{void f1} f1}","(int,null)"); }
	@Test public void test_1482() { checkNotSubtype("{{void f1} f1}","(int,int)"); }
	@Test public void test_1483() { checkNotSubtype("{{void f1} f1}","({any f1},any)"); }
	@Test public void test_1484() { checkNotSubtype("{{void f1} f1}","({any f2},any)"); }
	@Test public void test_1485() { checkNotSubtype("{{void f1} f1}","({null f1},null)"); }
	@Test public void test_1486() { checkNotSubtype("{{void f1} f1}","({null f2},null)"); }
	@Test public void test_1487() { checkNotSubtype("{{void f1} f1}","({int f1},int)"); }
	@Test public void test_1488() { checkNotSubtype("{{void f1} f1}","({int f2},int)"); }
	@Test public void test_1489() { checkIsSubtype("{{void f1} f1}","{{void f1} f1}"); }
	@Test public void test_1490() { checkIsSubtype("{{void f1} f1}","{{void f2} f1}"); }
	@Test public void test_1491() { checkIsSubtype("{{void f1} f1}","{{void f1} f2}"); }
	@Test public void test_1492() { checkIsSubtype("{{void f1} f1}","{{void f2} f2}"); }
	@Test public void test_1493() { checkNotSubtype("{{void f1} f1}","{{any f1} f1}"); }
	@Test public void test_1494() { checkNotSubtype("{{void f1} f1}","{{any f2} f1}"); }
	@Test public void test_1495() { checkNotSubtype("{{void f1} f1}","{{any f1} f2}"); }
	@Test public void test_1496() { checkNotSubtype("{{void f1} f1}","{{any f2} f2}"); }
	@Test public void test_1497() { checkNotSubtype("{{void f1} f1}","{{null f1} f1}"); }
	@Test public void test_1498() { checkNotSubtype("{{void f1} f1}","{{null f2} f1}"); }
	@Test public void test_1499() { checkNotSubtype("{{void f1} f1}","{{null f1} f2}"); }
	@Test public void test_1500() { checkNotSubtype("{{void f1} f1}","{{null f2} f2}"); }
	@Test public void test_1501() { checkNotSubtype("{{void f1} f1}","{{int f1} f1}"); }
	@Test public void test_1502() { checkNotSubtype("{{void f1} f1}","{{int f2} f1}"); }
	@Test public void test_1503() { checkNotSubtype("{{void f1} f1}","{{int f1} f2}"); }
	@Test public void test_1504() { checkNotSubtype("{{void f1} f1}","{{int f2} f2}"); }
	@Test public void test_1505() { checkNotSubtype("{{void f1} f1}","any"); }
	@Test public void test_1506() { checkNotSubtype("{{void f1} f1}","null"); }
	@Test public void test_1507() { checkNotSubtype("{{void f1} f1}","int"); }
	@Test public void test_1508() { checkNotSubtype("{{void f1} f1}","any"); }
	@Test public void test_1509() { checkNotSubtype("{{void f1} f1}","any"); }
	@Test public void test_1510() { checkNotSubtype("{{void f1} f1}","any"); }
	@Test public void test_1511() { checkNotSubtype("{{void f1} f1}","any"); }
	@Test public void test_1512() { checkNotSubtype("{{void f1} f1}","null"); }
	@Test public void test_1513() { checkNotSubtype("{{void f1} f1}","any"); }
	@Test public void test_1514() { checkNotSubtype("{{void f1} f1}","null"); }
	@Test public void test_1515() { checkNotSubtype("{{void f1} f1}","null|int"); }
	@Test public void test_1516() { checkNotSubtype("{{void f1} f1}","int"); }
	@Test public void test_1517() { checkNotSubtype("{{void f1} f1}","any"); }
	@Test public void test_1518() { checkNotSubtype("{{void f1} f1}","int|null"); }
	@Test public void test_1519() { checkNotSubtype("{{void f1} f1}","int"); }
	@Test public void test_1520() { checkNotSubtype("{{void f1} f1}","any"); }
	@Test public void test_1521() { checkNotSubtype("{{void f1} f1}","any"); }
	@Test public void test_1522() { checkNotSubtype("{{void f1} f1}","{null f1}|null"); }
	@Test public void test_1523() { checkNotSubtype("{{void f1} f1}","{null f2}|null"); }
	@Test public void test_1524() { checkNotSubtype("{{void f1} f1}","{int f1}|int"); }
	@Test public void test_1525() { checkNotSubtype("{{void f1} f1}","{int f2}|int"); }
	@Test public void test_1526() { checkNotSubtype("{{void f2} f1}","any"); }
	@Test public void test_1527() { checkNotSubtype("{{void f2} f1}","null"); }
	@Test public void test_1528() { checkNotSubtype("{{void f2} f1}","int"); }
	@Test public void test_1529() { checkNotSubtype("{{void f2} f1}","{any f1}"); }
	@Test public void test_1530() { checkNotSubtype("{{void f2} f1}","{any f2}"); }
	@Test public void test_1531() { checkNotSubtype("{{void f2} f1}","{null f1}"); }
	@Test public void test_1532() { checkNotSubtype("{{void f2} f1}","{null f2}"); }
	@Test public void test_1533() { checkNotSubtype("{{void f2} f1}","{int f1}"); }
	@Test public void test_1534() { checkNotSubtype("{{void f2} f1}","{int f2}"); }
	@Test public void test_1535() { checkNotSubtype("{{void f2} f1}","(any,any)"); }
	@Test public void test_1536() { checkNotSubtype("{{void f2} f1}","(any,null)"); }
	@Test public void test_1537() { checkNotSubtype("{{void f2} f1}","(any,int)"); }
	@Test public void test_1538() { checkNotSubtype("{{void f2} f1}","(null,any)"); }
	@Test public void test_1539() { checkNotSubtype("{{void f2} f1}","(null,null)"); }
	@Test public void test_1540() { checkNotSubtype("{{void f2} f1}","(null,int)"); }
	@Test public void test_1541() { checkNotSubtype("{{void f2} f1}","(int,any)"); }
	@Test public void test_1542() { checkNotSubtype("{{void f2} f1}","(int,null)"); }
	@Test public void test_1543() { checkNotSubtype("{{void f2} f1}","(int,int)"); }
	@Test public void test_1544() { checkNotSubtype("{{void f2} f1}","({any f1},any)"); }
	@Test public void test_1545() { checkNotSubtype("{{void f2} f1}","({any f2},any)"); }
	@Test public void test_1546() { checkNotSubtype("{{void f2} f1}","({null f1},null)"); }
	@Test public void test_1547() { checkNotSubtype("{{void f2} f1}","({null f2},null)"); }
	@Test public void test_1548() { checkNotSubtype("{{void f2} f1}","({int f1},int)"); }
	@Test public void test_1549() { checkNotSubtype("{{void f2} f1}","({int f2},int)"); }
	@Test public void test_1550() { checkIsSubtype("{{void f2} f1}","{{void f1} f1}"); }
	@Test public void test_1551() { checkIsSubtype("{{void f2} f1}","{{void f2} f1}"); }
	@Test public void test_1552() { checkIsSubtype("{{void f2} f1}","{{void f1} f2}"); }
	@Test public void test_1553() { checkIsSubtype("{{void f2} f1}","{{void f2} f2}"); }
	@Test public void test_1554() { checkNotSubtype("{{void f2} f1}","{{any f1} f1}"); }
	@Test public void test_1555() { checkNotSubtype("{{void f2} f1}","{{any f2} f1}"); }
	@Test public void test_1556() { checkNotSubtype("{{void f2} f1}","{{any f1} f2}"); }
	@Test public void test_1557() { checkNotSubtype("{{void f2} f1}","{{any f2} f2}"); }
	@Test public void test_1558() { checkNotSubtype("{{void f2} f1}","{{null f1} f1}"); }
	@Test public void test_1559() { checkNotSubtype("{{void f2} f1}","{{null f2} f1}"); }
	@Test public void test_1560() { checkNotSubtype("{{void f2} f1}","{{null f1} f2}"); }
	@Test public void test_1561() { checkNotSubtype("{{void f2} f1}","{{null f2} f2}"); }
	@Test public void test_1562() { checkNotSubtype("{{void f2} f1}","{{int f1} f1}"); }
	@Test public void test_1563() { checkNotSubtype("{{void f2} f1}","{{int f2} f1}"); }
	@Test public void test_1564() { checkNotSubtype("{{void f2} f1}","{{int f1} f2}"); }
	@Test public void test_1565() { checkNotSubtype("{{void f2} f1}","{{int f2} f2}"); }
	@Test public void test_1566() { checkNotSubtype("{{void f2} f1}","any"); }
	@Test public void test_1567() { checkNotSubtype("{{void f2} f1}","null"); }
	@Test public void test_1568() { checkNotSubtype("{{void f2} f1}","int"); }
	@Test public void test_1569() { checkNotSubtype("{{void f2} f1}","any"); }
	@Test public void test_1570() { checkNotSubtype("{{void f2} f1}","any"); }
	@Test public void test_1571() { checkNotSubtype("{{void f2} f1}","any"); }
	@Test public void test_1572() { checkNotSubtype("{{void f2} f1}","any"); }
	@Test public void test_1573() { checkNotSubtype("{{void f2} f1}","null"); }
	@Test public void test_1574() { checkNotSubtype("{{void f2} f1}","any"); }
	@Test public void test_1575() { checkNotSubtype("{{void f2} f1}","null"); }
	@Test public void test_1576() { checkNotSubtype("{{void f2} f1}","null|int"); }
	@Test public void test_1577() { checkNotSubtype("{{void f2} f1}","int"); }
	@Test public void test_1578() { checkNotSubtype("{{void f2} f1}","any"); }
	@Test public void test_1579() { checkNotSubtype("{{void f2} f1}","int|null"); }
	@Test public void test_1580() { checkNotSubtype("{{void f2} f1}","int"); }
	@Test public void test_1581() { checkNotSubtype("{{void f2} f1}","any"); }
	@Test public void test_1582() { checkNotSubtype("{{void f2} f1}","any"); }
	@Test public void test_1583() { checkNotSubtype("{{void f2} f1}","{null f1}|null"); }
	@Test public void test_1584() { checkNotSubtype("{{void f2} f1}","{null f2}|null"); }
	@Test public void test_1585() { checkNotSubtype("{{void f2} f1}","{int f1}|int"); }
	@Test public void test_1586() { checkNotSubtype("{{void f2} f1}","{int f2}|int"); }
	@Test public void test_1587() { checkNotSubtype("{{void f1} f2}","any"); }
	@Test public void test_1588() { checkNotSubtype("{{void f1} f2}","null"); }
	@Test public void test_1589() { checkNotSubtype("{{void f1} f2}","int"); }
	@Test public void test_1590() { checkNotSubtype("{{void f1} f2}","{any f1}"); }
	@Test public void test_1591() { checkNotSubtype("{{void f1} f2}","{any f2}"); }
	@Test public void test_1592() { checkNotSubtype("{{void f1} f2}","{null f1}"); }
	@Test public void test_1593() { checkNotSubtype("{{void f1} f2}","{null f2}"); }
	@Test public void test_1594() { checkNotSubtype("{{void f1} f2}","{int f1}"); }
	@Test public void test_1595() { checkNotSubtype("{{void f1} f2}","{int f2}"); }
	@Test public void test_1596() { checkNotSubtype("{{void f1} f2}","(any,any)"); }
	@Test public void test_1597() { checkNotSubtype("{{void f1} f2}","(any,null)"); }
	@Test public void test_1598() { checkNotSubtype("{{void f1} f2}","(any,int)"); }
	@Test public void test_1599() { checkNotSubtype("{{void f1} f2}","(null,any)"); }
	@Test public void test_1600() { checkNotSubtype("{{void f1} f2}","(null,null)"); }
	@Test public void test_1601() { checkNotSubtype("{{void f1} f2}","(null,int)"); }
	@Test public void test_1602() { checkNotSubtype("{{void f1} f2}","(int,any)"); }
	@Test public void test_1603() { checkNotSubtype("{{void f1} f2}","(int,null)"); }
	@Test public void test_1604() { checkNotSubtype("{{void f1} f2}","(int,int)"); }
	@Test public void test_1605() { checkNotSubtype("{{void f1} f2}","({any f1},any)"); }
	@Test public void test_1606() { checkNotSubtype("{{void f1} f2}","({any f2},any)"); }
	@Test public void test_1607() { checkNotSubtype("{{void f1} f2}","({null f1},null)"); }
	@Test public void test_1608() { checkNotSubtype("{{void f1} f2}","({null f2},null)"); }
	@Test public void test_1609() { checkNotSubtype("{{void f1} f2}","({int f1},int)"); }
	@Test public void test_1610() { checkNotSubtype("{{void f1} f2}","({int f2},int)"); }
	@Test public void test_1611() { checkIsSubtype("{{void f1} f2}","{{void f1} f1}"); }
	@Test public void test_1612() { checkIsSubtype("{{void f1} f2}","{{void f2} f1}"); }
	@Test public void test_1613() { checkIsSubtype("{{void f1} f2}","{{void f1} f2}"); }
	@Test public void test_1614() { checkIsSubtype("{{void f1} f2}","{{void f2} f2}"); }
	@Test public void test_1615() { checkNotSubtype("{{void f1} f2}","{{any f1} f1}"); }
	@Test public void test_1616() { checkNotSubtype("{{void f1} f2}","{{any f2} f1}"); }
	@Test public void test_1617() { checkNotSubtype("{{void f1} f2}","{{any f1} f2}"); }
	@Test public void test_1618() { checkNotSubtype("{{void f1} f2}","{{any f2} f2}"); }
	@Test public void test_1619() { checkNotSubtype("{{void f1} f2}","{{null f1} f1}"); }
	@Test public void test_1620() { checkNotSubtype("{{void f1} f2}","{{null f2} f1}"); }
	@Test public void test_1621() { checkNotSubtype("{{void f1} f2}","{{null f1} f2}"); }
	@Test public void test_1622() { checkNotSubtype("{{void f1} f2}","{{null f2} f2}"); }
	@Test public void test_1623() { checkNotSubtype("{{void f1} f2}","{{int f1} f1}"); }
	@Test public void test_1624() { checkNotSubtype("{{void f1} f2}","{{int f2} f1}"); }
	@Test public void test_1625() { checkNotSubtype("{{void f1} f2}","{{int f1} f2}"); }
	@Test public void test_1626() { checkNotSubtype("{{void f1} f2}","{{int f2} f2}"); }
	@Test public void test_1627() { checkNotSubtype("{{void f1} f2}","any"); }
	@Test public void test_1628() { checkNotSubtype("{{void f1} f2}","null"); }
	@Test public void test_1629() { checkNotSubtype("{{void f1} f2}","int"); }
	@Test public void test_1630() { checkNotSubtype("{{void f1} f2}","any"); }
	@Test public void test_1631() { checkNotSubtype("{{void f1} f2}","any"); }
	@Test public void test_1632() { checkNotSubtype("{{void f1} f2}","any"); }
	@Test public void test_1633() { checkNotSubtype("{{void f1} f2}","any"); }
	@Test public void test_1634() { checkNotSubtype("{{void f1} f2}","null"); }
	@Test public void test_1635() { checkNotSubtype("{{void f1} f2}","any"); }
	@Test public void test_1636() { checkNotSubtype("{{void f1} f2}","null"); }
	@Test public void test_1637() { checkNotSubtype("{{void f1} f2}","null|int"); }
	@Test public void test_1638() { checkNotSubtype("{{void f1} f2}","int"); }
	@Test public void test_1639() { checkNotSubtype("{{void f1} f2}","any"); }
	@Test public void test_1640() { checkNotSubtype("{{void f1} f2}","int|null"); }
	@Test public void test_1641() { checkNotSubtype("{{void f1} f2}","int"); }
	@Test public void test_1642() { checkNotSubtype("{{void f1} f2}","any"); }
	@Test public void test_1643() { checkNotSubtype("{{void f1} f2}","any"); }
	@Test public void test_1644() { checkNotSubtype("{{void f1} f2}","{null f1}|null"); }
	@Test public void test_1645() { checkNotSubtype("{{void f1} f2}","{null f2}|null"); }
	@Test public void test_1646() { checkNotSubtype("{{void f1} f2}","{int f1}|int"); }
	@Test public void test_1647() { checkNotSubtype("{{void f1} f2}","{int f2}|int"); }
	@Test public void test_1648() { checkNotSubtype("{{void f2} f2}","any"); }
	@Test public void test_1649() { checkNotSubtype("{{void f2} f2}","null"); }
	@Test public void test_1650() { checkNotSubtype("{{void f2} f2}","int"); }
	@Test public void test_1651() { checkNotSubtype("{{void f2} f2}","{any f1}"); }
	@Test public void test_1652() { checkNotSubtype("{{void f2} f2}","{any f2}"); }
	@Test public void test_1653() { checkNotSubtype("{{void f2} f2}","{null f1}"); }
	@Test public void test_1654() { checkNotSubtype("{{void f2} f2}","{null f2}"); }
	@Test public void test_1655() { checkNotSubtype("{{void f2} f2}","{int f1}"); }
	@Test public void test_1656() { checkNotSubtype("{{void f2} f2}","{int f2}"); }
	@Test public void test_1657() { checkNotSubtype("{{void f2} f2}","(any,any)"); }
	@Test public void test_1658() { checkNotSubtype("{{void f2} f2}","(any,null)"); }
	@Test public void test_1659() { checkNotSubtype("{{void f2} f2}","(any,int)"); }
	@Test public void test_1660() { checkNotSubtype("{{void f2} f2}","(null,any)"); }
	@Test public void test_1661() { checkNotSubtype("{{void f2} f2}","(null,null)"); }
	@Test public void test_1662() { checkNotSubtype("{{void f2} f2}","(null,int)"); }
	@Test public void test_1663() { checkNotSubtype("{{void f2} f2}","(int,any)"); }
	@Test public void test_1664() { checkNotSubtype("{{void f2} f2}","(int,null)"); }
	@Test public void test_1665() { checkNotSubtype("{{void f2} f2}","(int,int)"); }
	@Test public void test_1666() { checkNotSubtype("{{void f2} f2}","({any f1},any)"); }
	@Test public void test_1667() { checkNotSubtype("{{void f2} f2}","({any f2},any)"); }
	@Test public void test_1668() { checkNotSubtype("{{void f2} f2}","({null f1},null)"); }
	@Test public void test_1669() { checkNotSubtype("{{void f2} f2}","({null f2},null)"); }
	@Test public void test_1670() { checkNotSubtype("{{void f2} f2}","({int f1},int)"); }
	@Test public void test_1671() { checkNotSubtype("{{void f2} f2}","({int f2},int)"); }
	@Test public void test_1672() { checkIsSubtype("{{void f2} f2}","{{void f1} f1}"); }
	@Test public void test_1673() { checkIsSubtype("{{void f2} f2}","{{void f2} f1}"); }
	@Test public void test_1674() { checkIsSubtype("{{void f2} f2}","{{void f1} f2}"); }
	@Test public void test_1675() { checkIsSubtype("{{void f2} f2}","{{void f2} f2}"); }
	@Test public void test_1676() { checkNotSubtype("{{void f2} f2}","{{any f1} f1}"); }
	@Test public void test_1677() { checkNotSubtype("{{void f2} f2}","{{any f2} f1}"); }
	@Test public void test_1678() { checkNotSubtype("{{void f2} f2}","{{any f1} f2}"); }
	@Test public void test_1679() { checkNotSubtype("{{void f2} f2}","{{any f2} f2}"); }
	@Test public void test_1680() { checkNotSubtype("{{void f2} f2}","{{null f1} f1}"); }
	@Test public void test_1681() { checkNotSubtype("{{void f2} f2}","{{null f2} f1}"); }
	@Test public void test_1682() { checkNotSubtype("{{void f2} f2}","{{null f1} f2}"); }
	@Test public void test_1683() { checkNotSubtype("{{void f2} f2}","{{null f2} f2}"); }
	@Test public void test_1684() { checkNotSubtype("{{void f2} f2}","{{int f1} f1}"); }
	@Test public void test_1685() { checkNotSubtype("{{void f2} f2}","{{int f2} f1}"); }
	@Test public void test_1686() { checkNotSubtype("{{void f2} f2}","{{int f1} f2}"); }
	@Test public void test_1687() { checkNotSubtype("{{void f2} f2}","{{int f2} f2}"); }
	@Test public void test_1688() { checkNotSubtype("{{void f2} f2}","any"); }
	@Test public void test_1689() { checkNotSubtype("{{void f2} f2}","null"); }
	@Test public void test_1690() { checkNotSubtype("{{void f2} f2}","int"); }
	@Test public void test_1691() { checkNotSubtype("{{void f2} f2}","any"); }
	@Test public void test_1692() { checkNotSubtype("{{void f2} f2}","any"); }
	@Test public void test_1693() { checkNotSubtype("{{void f2} f2}","any"); }
	@Test public void test_1694() { checkNotSubtype("{{void f2} f2}","any"); }
	@Test public void test_1695() { checkNotSubtype("{{void f2} f2}","null"); }
	@Test public void test_1696() { checkNotSubtype("{{void f2} f2}","any"); }
	@Test public void test_1697() { checkNotSubtype("{{void f2} f2}","null"); }
	@Test public void test_1698() { checkNotSubtype("{{void f2} f2}","null|int"); }
	@Test public void test_1699() { checkNotSubtype("{{void f2} f2}","int"); }
	@Test public void test_1700() { checkNotSubtype("{{void f2} f2}","any"); }
	@Test public void test_1701() { checkNotSubtype("{{void f2} f2}","int|null"); }
	@Test public void test_1702() { checkNotSubtype("{{void f2} f2}","int"); }
	@Test public void test_1703() { checkNotSubtype("{{void f2} f2}","any"); }
	@Test public void test_1704() { checkNotSubtype("{{void f2} f2}","any"); }
	@Test public void test_1705() { checkNotSubtype("{{void f2} f2}","{null f1}|null"); }
	@Test public void test_1706() { checkNotSubtype("{{void f2} f2}","{null f2}|null"); }
	@Test public void test_1707() { checkNotSubtype("{{void f2} f2}","{int f1}|int"); }
	@Test public void test_1708() { checkNotSubtype("{{void f2} f2}","{int f2}|int"); }
	@Test public void test_1709() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1710() { checkNotSubtype("{{any f1} f1}","null"); }
	@Test public void test_1711() { checkNotSubtype("{{any f1} f1}","int"); }
	@Test public void test_1712() { checkNotSubtype("{{any f1} f1}","{any f1}"); }
	@Test public void test_1713() { checkNotSubtype("{{any f1} f1}","{any f2}"); }
	@Test public void test_1714() { checkNotSubtype("{{any f1} f1}","{null f1}"); }
	@Test public void test_1715() { checkNotSubtype("{{any f1} f1}","{null f2}"); }
	@Test public void test_1716() { checkNotSubtype("{{any f1} f1}","{int f1}"); }
	@Test public void test_1717() { checkNotSubtype("{{any f1} f1}","{int f2}"); }
	@Test public void test_1718() { checkNotSubtype("{{any f1} f1}","(any,any)"); }
	@Test public void test_1719() { checkNotSubtype("{{any f1} f1}","(any,null)"); }
	@Test public void test_1720() { checkNotSubtype("{{any f1} f1}","(any,int)"); }
	@Test public void test_1721() { checkNotSubtype("{{any f1} f1}","(null,any)"); }
	@Test public void test_1722() { checkNotSubtype("{{any f1} f1}","(null,null)"); }
	@Test public void test_1723() { checkNotSubtype("{{any f1} f1}","(null,int)"); }
	@Test public void test_1724() { checkNotSubtype("{{any f1} f1}","(int,any)"); }
	@Test public void test_1725() { checkNotSubtype("{{any f1} f1}","(int,null)"); }
	@Test public void test_1726() { checkNotSubtype("{{any f1} f1}","(int,int)"); }
	@Test public void test_1727() { checkNotSubtype("{{any f1} f1}","({any f1},any)"); }
	@Test public void test_1728() { checkNotSubtype("{{any f1} f1}","({any f2},any)"); }
	@Test public void test_1729() { checkNotSubtype("{{any f1} f1}","({null f1},null)"); }
	@Test public void test_1730() { checkNotSubtype("{{any f1} f1}","({null f2},null)"); }
	@Test public void test_1731() { checkNotSubtype("{{any f1} f1}","({int f1},int)"); }
	@Test public void test_1732() { checkNotSubtype("{{any f1} f1}","({int f2},int)"); }
	@Test public void test_1733() { checkIsSubtype("{{any f1} f1}","{{void f1} f1}"); }
	@Test public void test_1734() { checkIsSubtype("{{any f1} f1}","{{void f2} f1}"); }
	@Test public void test_1735() { checkIsSubtype("{{any f1} f1}","{{void f1} f2}"); }
	@Test public void test_1736() { checkIsSubtype("{{any f1} f1}","{{void f2} f2}"); }
	@Test public void test_1737() { checkIsSubtype("{{any f1} f1}","{{any f1} f1}"); }
	@Test public void test_1738() { checkNotSubtype("{{any f1} f1}","{{any f2} f1}"); }
	@Test public void test_1739() { checkNotSubtype("{{any f1} f1}","{{any f1} f2}"); }
	@Test public void test_1740() { checkNotSubtype("{{any f1} f1}","{{any f2} f2}"); }
	@Test public void test_1741() { checkIsSubtype("{{any f1} f1}","{{null f1} f1}"); }
	@Test public void test_1742() { checkNotSubtype("{{any f1} f1}","{{null f2} f1}"); }
	@Test public void test_1743() { checkNotSubtype("{{any f1} f1}","{{null f1} f2}"); }
	@Test public void test_1744() { checkNotSubtype("{{any f1} f1}","{{null f2} f2}"); }
	@Test public void test_1745() { checkIsSubtype("{{any f1} f1}","{{int f1} f1}"); }
	@Test public void test_1746() { checkNotSubtype("{{any f1} f1}","{{int f2} f1}"); }
	@Test public void test_1747() { checkNotSubtype("{{any f1} f1}","{{int f1} f2}"); }
	@Test public void test_1748() { checkNotSubtype("{{any f1} f1}","{{int f2} f2}"); }
	@Test public void test_1749() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1750() { checkNotSubtype("{{any f1} f1}","null"); }
	@Test public void test_1751() { checkNotSubtype("{{any f1} f1}","int"); }
	@Test public void test_1752() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1753() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1754() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1755() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1756() { checkNotSubtype("{{any f1} f1}","null"); }
	@Test public void test_1757() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1758() { checkNotSubtype("{{any f1} f1}","null"); }
	@Test public void test_1759() { checkNotSubtype("{{any f1} f1}","null|int"); }
	@Test public void test_1760() { checkNotSubtype("{{any f1} f1}","int"); }
	@Test public void test_1761() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1762() { checkNotSubtype("{{any f1} f1}","int|null"); }
	@Test public void test_1763() { checkNotSubtype("{{any f1} f1}","int"); }
	@Test public void test_1764() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1765() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1766() { checkNotSubtype("{{any f1} f1}","{null f1}|null"); }
	@Test public void test_1767() { checkNotSubtype("{{any f1} f1}","{null f2}|null"); }
	@Test public void test_1768() { checkNotSubtype("{{any f1} f1}","{int f1}|int"); }
	@Test public void test_1769() { checkNotSubtype("{{any f1} f1}","{int f2}|int"); }
	@Test public void test_1770() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1771() { checkNotSubtype("{{any f2} f1}","null"); }
	@Test public void test_1772() { checkNotSubtype("{{any f2} f1}","int"); }
	@Test public void test_1773() { checkNotSubtype("{{any f2} f1}","{any f1}"); }
	@Test public void test_1774() { checkNotSubtype("{{any f2} f1}","{any f2}"); }
	@Test public void test_1775() { checkNotSubtype("{{any f2} f1}","{null f1}"); }
	@Test public void test_1776() { checkNotSubtype("{{any f2} f1}","{null f2}"); }
	@Test public void test_1777() { checkNotSubtype("{{any f2} f1}","{int f1}"); }
	@Test public void test_1778() { checkNotSubtype("{{any f2} f1}","{int f2}"); }
	@Test public void test_1779() { checkNotSubtype("{{any f2} f1}","(any,any)"); }
	@Test public void test_1780() { checkNotSubtype("{{any f2} f1}","(any,null)"); }
	@Test public void test_1781() { checkNotSubtype("{{any f2} f1}","(any,int)"); }
	@Test public void test_1782() { checkNotSubtype("{{any f2} f1}","(null,any)"); }
	@Test public void test_1783() { checkNotSubtype("{{any f2} f1}","(null,null)"); }
	@Test public void test_1784() { checkNotSubtype("{{any f2} f1}","(null,int)"); }
	@Test public void test_1785() { checkNotSubtype("{{any f2} f1}","(int,any)"); }
	@Test public void test_1786() { checkNotSubtype("{{any f2} f1}","(int,null)"); }
	@Test public void test_1787() { checkNotSubtype("{{any f2} f1}","(int,int)"); }
	@Test public void test_1788() { checkNotSubtype("{{any f2} f1}","({any f1},any)"); }
	@Test public void test_1789() { checkNotSubtype("{{any f2} f1}","({any f2},any)"); }
	@Test public void test_1790() { checkNotSubtype("{{any f2} f1}","({null f1},null)"); }
	@Test public void test_1791() { checkNotSubtype("{{any f2} f1}","({null f2},null)"); }
	@Test public void test_1792() { checkNotSubtype("{{any f2} f1}","({int f1},int)"); }
	@Test public void test_1793() { checkNotSubtype("{{any f2} f1}","({int f2},int)"); }
	@Test public void test_1794() { checkIsSubtype("{{any f2} f1}","{{void f1} f1}"); }
	@Test public void test_1795() { checkIsSubtype("{{any f2} f1}","{{void f2} f1}"); }
	@Test public void test_1796() { checkIsSubtype("{{any f2} f1}","{{void f1} f2}"); }
	@Test public void test_1797() { checkIsSubtype("{{any f2} f1}","{{void f2} f2}"); }
	@Test public void test_1798() { checkNotSubtype("{{any f2} f1}","{{any f1} f1}"); }
	@Test public void test_1799() { checkIsSubtype("{{any f2} f1}","{{any f2} f1}"); }
	@Test public void test_1800() { checkNotSubtype("{{any f2} f1}","{{any f1} f2}"); }
	@Test public void test_1801() { checkNotSubtype("{{any f2} f1}","{{any f2} f2}"); }
	@Test public void test_1802() { checkNotSubtype("{{any f2} f1}","{{null f1} f1}"); }
	@Test public void test_1803() { checkIsSubtype("{{any f2} f1}","{{null f2} f1}"); }
	@Test public void test_1804() { checkNotSubtype("{{any f2} f1}","{{null f1} f2}"); }
	@Test public void test_1805() { checkNotSubtype("{{any f2} f1}","{{null f2} f2}"); }
	@Test public void test_1806() { checkNotSubtype("{{any f2} f1}","{{int f1} f1}"); }
	@Test public void test_1807() { checkIsSubtype("{{any f2} f1}","{{int f2} f1}"); }
	@Test public void test_1808() { checkNotSubtype("{{any f2} f1}","{{int f1} f2}"); }
	@Test public void test_1809() { checkNotSubtype("{{any f2} f1}","{{int f2} f2}"); }
	@Test public void test_1810() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1811() { checkNotSubtype("{{any f2} f1}","null"); }
	@Test public void test_1812() { checkNotSubtype("{{any f2} f1}","int"); }
	@Test public void test_1813() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1814() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1815() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1816() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1817() { checkNotSubtype("{{any f2} f1}","null"); }
	@Test public void test_1818() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1819() { checkNotSubtype("{{any f2} f1}","null"); }
	@Test public void test_1820() { checkNotSubtype("{{any f2} f1}","null|int"); }
	@Test public void test_1821() { checkNotSubtype("{{any f2} f1}","int"); }
	@Test public void test_1822() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1823() { checkNotSubtype("{{any f2} f1}","int|null"); }
	@Test public void test_1824() { checkNotSubtype("{{any f2} f1}","int"); }
	@Test public void test_1825() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1826() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1827() { checkNotSubtype("{{any f2} f1}","{null f1}|null"); }
	@Test public void test_1828() { checkNotSubtype("{{any f2} f1}","{null f2}|null"); }
	@Test public void test_1829() { checkNotSubtype("{{any f2} f1}","{int f1}|int"); }
	@Test public void test_1830() { checkNotSubtype("{{any f2} f1}","{int f2}|int"); }
	@Test public void test_1831() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1832() { checkNotSubtype("{{any f1} f2}","null"); }
	@Test public void test_1833() { checkNotSubtype("{{any f1} f2}","int"); }
	@Test public void test_1834() { checkNotSubtype("{{any f1} f2}","{any f1}"); }
	@Test public void test_1835() { checkNotSubtype("{{any f1} f2}","{any f2}"); }
	@Test public void test_1836() { checkNotSubtype("{{any f1} f2}","{null f1}"); }
	@Test public void test_1837() { checkNotSubtype("{{any f1} f2}","{null f2}"); }
	@Test public void test_1838() { checkNotSubtype("{{any f1} f2}","{int f1}"); }
	@Test public void test_1839() { checkNotSubtype("{{any f1} f2}","{int f2}"); }
	@Test public void test_1840() { checkNotSubtype("{{any f1} f2}","(any,any)"); }
	@Test public void test_1841() { checkNotSubtype("{{any f1} f2}","(any,null)"); }
	@Test public void test_1842() { checkNotSubtype("{{any f1} f2}","(any,int)"); }
	@Test public void test_1843() { checkNotSubtype("{{any f1} f2}","(null,any)"); }
	@Test public void test_1844() { checkNotSubtype("{{any f1} f2}","(null,null)"); }
	@Test public void test_1845() { checkNotSubtype("{{any f1} f2}","(null,int)"); }
	@Test public void test_1846() { checkNotSubtype("{{any f1} f2}","(int,any)"); }
	@Test public void test_1847() { checkNotSubtype("{{any f1} f2}","(int,null)"); }
	@Test public void test_1848() { checkNotSubtype("{{any f1} f2}","(int,int)"); }
	@Test public void test_1849() { checkNotSubtype("{{any f1} f2}","({any f1},any)"); }
	@Test public void test_1850() { checkNotSubtype("{{any f1} f2}","({any f2},any)"); }
	@Test public void test_1851() { checkNotSubtype("{{any f1} f2}","({null f1},null)"); }
	@Test public void test_1852() { checkNotSubtype("{{any f1} f2}","({null f2},null)"); }
	@Test public void test_1853() { checkNotSubtype("{{any f1} f2}","({int f1},int)"); }
	@Test public void test_1854() { checkNotSubtype("{{any f1} f2}","({int f2},int)"); }
	@Test public void test_1855() { checkIsSubtype("{{any f1} f2}","{{void f1} f1}"); }
	@Test public void test_1856() { checkIsSubtype("{{any f1} f2}","{{void f2} f1}"); }
	@Test public void test_1857() { checkIsSubtype("{{any f1} f2}","{{void f1} f2}"); }
	@Test public void test_1858() { checkIsSubtype("{{any f1} f2}","{{void f2} f2}"); }
	@Test public void test_1859() { checkNotSubtype("{{any f1} f2}","{{any f1} f1}"); }
	@Test public void test_1860() { checkNotSubtype("{{any f1} f2}","{{any f2} f1}"); }
	@Test public void test_1861() { checkIsSubtype("{{any f1} f2}","{{any f1} f2}"); }
	@Test public void test_1862() { checkNotSubtype("{{any f1} f2}","{{any f2} f2}"); }
	@Test public void test_1863() { checkNotSubtype("{{any f1} f2}","{{null f1} f1}"); }
	@Test public void test_1864() { checkNotSubtype("{{any f1} f2}","{{null f2} f1}"); }
	@Test public void test_1865() { checkIsSubtype("{{any f1} f2}","{{null f1} f2}"); }
	@Test public void test_1866() { checkNotSubtype("{{any f1} f2}","{{null f2} f2}"); }
	@Test public void test_1867() { checkNotSubtype("{{any f1} f2}","{{int f1} f1}"); }
	@Test public void test_1868() { checkNotSubtype("{{any f1} f2}","{{int f2} f1}"); }
	@Test public void test_1869() { checkIsSubtype("{{any f1} f2}","{{int f1} f2}"); }
	@Test public void test_1870() { checkNotSubtype("{{any f1} f2}","{{int f2} f2}"); }
	@Test public void test_1871() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1872() { checkNotSubtype("{{any f1} f2}","null"); }
	@Test public void test_1873() { checkNotSubtype("{{any f1} f2}","int"); }
	@Test public void test_1874() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1875() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1876() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1877() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1878() { checkNotSubtype("{{any f1} f2}","null"); }
	@Test public void test_1879() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1880() { checkNotSubtype("{{any f1} f2}","null"); }
	@Test public void test_1881() { checkNotSubtype("{{any f1} f2}","null|int"); }
	@Test public void test_1882() { checkNotSubtype("{{any f1} f2}","int"); }
	@Test public void test_1883() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1884() { checkNotSubtype("{{any f1} f2}","int|null"); }
	@Test public void test_1885() { checkNotSubtype("{{any f1} f2}","int"); }
	@Test public void test_1886() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1887() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1888() { checkNotSubtype("{{any f1} f2}","{null f1}|null"); }
	@Test public void test_1889() { checkNotSubtype("{{any f1} f2}","{null f2}|null"); }
	@Test public void test_1890() { checkNotSubtype("{{any f1} f2}","{int f1}|int"); }
	@Test public void test_1891() { checkNotSubtype("{{any f1} f2}","{int f2}|int"); }
	@Test public void test_1892() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1893() { checkNotSubtype("{{any f2} f2}","null"); }
	@Test public void test_1894() { checkNotSubtype("{{any f2} f2}","int"); }
	@Test public void test_1895() { checkNotSubtype("{{any f2} f2}","{any f1}"); }
	@Test public void test_1896() { checkNotSubtype("{{any f2} f2}","{any f2}"); }
	@Test public void test_1897() { checkNotSubtype("{{any f2} f2}","{null f1}"); }
	@Test public void test_1898() { checkNotSubtype("{{any f2} f2}","{null f2}"); }
	@Test public void test_1899() { checkNotSubtype("{{any f2} f2}","{int f1}"); }
	@Test public void test_1900() { checkNotSubtype("{{any f2} f2}","{int f2}"); }
	@Test public void test_1901() { checkNotSubtype("{{any f2} f2}","(any,any)"); }
	@Test public void test_1902() { checkNotSubtype("{{any f2} f2}","(any,null)"); }
	@Test public void test_1903() { checkNotSubtype("{{any f2} f2}","(any,int)"); }
	@Test public void test_1904() { checkNotSubtype("{{any f2} f2}","(null,any)"); }
	@Test public void test_1905() { checkNotSubtype("{{any f2} f2}","(null,null)"); }
	@Test public void test_1906() { checkNotSubtype("{{any f2} f2}","(null,int)"); }
	@Test public void test_1907() { checkNotSubtype("{{any f2} f2}","(int,any)"); }
	@Test public void test_1908() { checkNotSubtype("{{any f2} f2}","(int,null)"); }
	@Test public void test_1909() { checkNotSubtype("{{any f2} f2}","(int,int)"); }
	@Test public void test_1910() { checkNotSubtype("{{any f2} f2}","({any f1},any)"); }
	@Test public void test_1911() { checkNotSubtype("{{any f2} f2}","({any f2},any)"); }
	@Test public void test_1912() { checkNotSubtype("{{any f2} f2}","({null f1},null)"); }
	@Test public void test_1913() { checkNotSubtype("{{any f2} f2}","({null f2},null)"); }
	@Test public void test_1914() { checkNotSubtype("{{any f2} f2}","({int f1},int)"); }
	@Test public void test_1915() { checkNotSubtype("{{any f2} f2}","({int f2},int)"); }
	@Test public void test_1916() { checkIsSubtype("{{any f2} f2}","{{void f1} f1}"); }
	@Test public void test_1917() { checkIsSubtype("{{any f2} f2}","{{void f2} f1}"); }
	@Test public void test_1918() { checkIsSubtype("{{any f2} f2}","{{void f1} f2}"); }
	@Test public void test_1919() { checkIsSubtype("{{any f2} f2}","{{void f2} f2}"); }
	@Test public void test_1920() { checkNotSubtype("{{any f2} f2}","{{any f1} f1}"); }
	@Test public void test_1921() { checkNotSubtype("{{any f2} f2}","{{any f2} f1}"); }
	@Test public void test_1922() { checkNotSubtype("{{any f2} f2}","{{any f1} f2}"); }
	@Test public void test_1923() { checkIsSubtype("{{any f2} f2}","{{any f2} f2}"); }
	@Test public void test_1924() { checkNotSubtype("{{any f2} f2}","{{null f1} f1}"); }
	@Test public void test_1925() { checkNotSubtype("{{any f2} f2}","{{null f2} f1}"); }
	@Test public void test_1926() { checkNotSubtype("{{any f2} f2}","{{null f1} f2}"); }
	@Test public void test_1927() { checkIsSubtype("{{any f2} f2}","{{null f2} f2}"); }
	@Test public void test_1928() { checkNotSubtype("{{any f2} f2}","{{int f1} f1}"); }
	@Test public void test_1929() { checkNotSubtype("{{any f2} f2}","{{int f2} f1}"); }
	@Test public void test_1930() { checkNotSubtype("{{any f2} f2}","{{int f1} f2}"); }
	@Test public void test_1931() { checkIsSubtype("{{any f2} f2}","{{int f2} f2}"); }
	@Test public void test_1932() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1933() { checkNotSubtype("{{any f2} f2}","null"); }
	@Test public void test_1934() { checkNotSubtype("{{any f2} f2}","int"); }
	@Test public void test_1935() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1936() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1937() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1938() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1939() { checkNotSubtype("{{any f2} f2}","null"); }
	@Test public void test_1940() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1941() { checkNotSubtype("{{any f2} f2}","null"); }
	@Test public void test_1942() { checkNotSubtype("{{any f2} f2}","null|int"); }
	@Test public void test_1943() { checkNotSubtype("{{any f2} f2}","int"); }
	@Test public void test_1944() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1945() { checkNotSubtype("{{any f2} f2}","int|null"); }
	@Test public void test_1946() { checkNotSubtype("{{any f2} f2}","int"); }
	@Test public void test_1947() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1948() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1949() { checkNotSubtype("{{any f2} f2}","{null f1}|null"); }
	@Test public void test_1950() { checkNotSubtype("{{any f2} f2}","{null f2}|null"); }
	@Test public void test_1951() { checkNotSubtype("{{any f2} f2}","{int f1}|int"); }
	@Test public void test_1952() { checkNotSubtype("{{any f2} f2}","{int f2}|int"); }
	@Test public void test_1953() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_1954() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test public void test_1955() { checkNotSubtype("{{null f1} f1}","int"); }
	@Test public void test_1956() { checkNotSubtype("{{null f1} f1}","{any f1}"); }
	@Test public void test_1957() { checkNotSubtype("{{null f1} f1}","{any f2}"); }
	@Test public void test_1958() { checkNotSubtype("{{null f1} f1}","{null f1}"); }
	@Test public void test_1959() { checkNotSubtype("{{null f1} f1}","{null f2}"); }
	@Test public void test_1960() { checkNotSubtype("{{null f1} f1}","{int f1}"); }
	@Test public void test_1961() { checkNotSubtype("{{null f1} f1}","{int f2}"); }
	@Test public void test_1962() { checkNotSubtype("{{null f1} f1}","(any,any)"); }
	@Test public void test_1963() { checkNotSubtype("{{null f1} f1}","(any,null)"); }
	@Test public void test_1964() { checkNotSubtype("{{null f1} f1}","(any,int)"); }
	@Test public void test_1965() { checkNotSubtype("{{null f1} f1}","(null,any)"); }
	@Test public void test_1966() { checkNotSubtype("{{null f1} f1}","(null,null)"); }
	@Test public void test_1967() { checkNotSubtype("{{null f1} f1}","(null,int)"); }
	@Test public void test_1968() { checkNotSubtype("{{null f1} f1}","(int,any)"); }
	@Test public void test_1969() { checkNotSubtype("{{null f1} f1}","(int,null)"); }
	@Test public void test_1970() { checkNotSubtype("{{null f1} f1}","(int,int)"); }
	@Test public void test_1971() { checkNotSubtype("{{null f1} f1}","({any f1},any)"); }
	@Test public void test_1972() { checkNotSubtype("{{null f1} f1}","({any f2},any)"); }
	@Test public void test_1973() { checkNotSubtype("{{null f1} f1}","({null f1},null)"); }
	@Test public void test_1974() { checkNotSubtype("{{null f1} f1}","({null f2},null)"); }
	@Test public void test_1975() { checkNotSubtype("{{null f1} f1}","({int f1},int)"); }
	@Test public void test_1976() { checkNotSubtype("{{null f1} f1}","({int f2},int)"); }
	@Test public void test_1977() { checkIsSubtype("{{null f1} f1}","{{void f1} f1}"); }
	@Test public void test_1978() { checkIsSubtype("{{null f1} f1}","{{void f2} f1}"); }
	@Test public void test_1979() { checkIsSubtype("{{null f1} f1}","{{void f1} f2}"); }
	@Test public void test_1980() { checkIsSubtype("{{null f1} f1}","{{void f2} f2}"); }
	@Test public void test_1981() { checkNotSubtype("{{null f1} f1}","{{any f1} f1}"); }
	@Test public void test_1982() { checkNotSubtype("{{null f1} f1}","{{any f2} f1}"); }
	@Test public void test_1983() { checkNotSubtype("{{null f1} f1}","{{any f1} f2}"); }
	@Test public void test_1984() { checkNotSubtype("{{null f1} f1}","{{any f2} f2}"); }
	@Test public void test_1985() { checkIsSubtype("{{null f1} f1}","{{null f1} f1}"); }
	@Test public void test_1986() { checkNotSubtype("{{null f1} f1}","{{null f2} f1}"); }
	@Test public void test_1987() { checkNotSubtype("{{null f1} f1}","{{null f1} f2}"); }
	@Test public void test_1988() { checkNotSubtype("{{null f1} f1}","{{null f2} f2}"); }
	@Test public void test_1989() { checkNotSubtype("{{null f1} f1}","{{int f1} f1}"); }
	@Test public void test_1990() { checkNotSubtype("{{null f1} f1}","{{int f2} f1}"); }
	@Test public void test_1991() { checkNotSubtype("{{null f1} f1}","{{int f1} f2}"); }
	@Test public void test_1992() { checkNotSubtype("{{null f1} f1}","{{int f2} f2}"); }
	@Test public void test_1993() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_1994() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test public void test_1995() { checkNotSubtype("{{null f1} f1}","int"); }
	@Test public void test_1996() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_1997() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_1998() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_1999() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_2000() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test public void test_2001() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_2002() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test public void test_2003() { checkNotSubtype("{{null f1} f1}","null|int"); }
	@Test public void test_2004() { checkNotSubtype("{{null f1} f1}","int"); }
	@Test public void test_2005() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_2006() { checkNotSubtype("{{null f1} f1}","int|null"); }
	@Test public void test_2007() { checkNotSubtype("{{null f1} f1}","int"); }
	@Test public void test_2008() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_2009() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_2010() { checkNotSubtype("{{null f1} f1}","{null f1}|null"); }
	@Test public void test_2011() { checkNotSubtype("{{null f1} f1}","{null f2}|null"); }
	@Test public void test_2012() { checkNotSubtype("{{null f1} f1}","{int f1}|int"); }
	@Test public void test_2013() { checkNotSubtype("{{null f1} f1}","{int f2}|int"); }
	@Test public void test_2014() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_2015() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test public void test_2016() { checkNotSubtype("{{null f2} f1}","int"); }
	@Test public void test_2017() { checkNotSubtype("{{null f2} f1}","{any f1}"); }
	@Test public void test_2018() { checkNotSubtype("{{null f2} f1}","{any f2}"); }
	@Test public void test_2019() { checkNotSubtype("{{null f2} f1}","{null f1}"); }
	@Test public void test_2020() { checkNotSubtype("{{null f2} f1}","{null f2}"); }
	@Test public void test_2021() { checkNotSubtype("{{null f2} f1}","{int f1}"); }
	@Test public void test_2022() { checkNotSubtype("{{null f2} f1}","{int f2}"); }
	@Test public void test_2023() { checkNotSubtype("{{null f2} f1}","(any,any)"); }
	@Test public void test_2024() { checkNotSubtype("{{null f2} f1}","(any,null)"); }
	@Test public void test_2025() { checkNotSubtype("{{null f2} f1}","(any,int)"); }
	@Test public void test_2026() { checkNotSubtype("{{null f2} f1}","(null,any)"); }
	@Test public void test_2027() { checkNotSubtype("{{null f2} f1}","(null,null)"); }
	@Test public void test_2028() { checkNotSubtype("{{null f2} f1}","(null,int)"); }
	@Test public void test_2029() { checkNotSubtype("{{null f2} f1}","(int,any)"); }
	@Test public void test_2030() { checkNotSubtype("{{null f2} f1}","(int,null)"); }
	@Test public void test_2031() { checkNotSubtype("{{null f2} f1}","(int,int)"); }
	@Test public void test_2032() { checkNotSubtype("{{null f2} f1}","({any f1},any)"); }
	@Test public void test_2033() { checkNotSubtype("{{null f2} f1}","({any f2},any)"); }
	@Test public void test_2034() { checkNotSubtype("{{null f2} f1}","({null f1},null)"); }
	@Test public void test_2035() { checkNotSubtype("{{null f2} f1}","({null f2},null)"); }
	@Test public void test_2036() { checkNotSubtype("{{null f2} f1}","({int f1},int)"); }
	@Test public void test_2037() { checkNotSubtype("{{null f2} f1}","({int f2},int)"); }
	@Test public void test_2038() { checkIsSubtype("{{null f2} f1}","{{void f1} f1}"); }
	@Test public void test_2039() { checkIsSubtype("{{null f2} f1}","{{void f2} f1}"); }
	@Test public void test_2040() { checkIsSubtype("{{null f2} f1}","{{void f1} f2}"); }
	@Test public void test_2041() { checkIsSubtype("{{null f2} f1}","{{void f2} f2}"); }
	@Test public void test_2042() { checkNotSubtype("{{null f2} f1}","{{any f1} f1}"); }
	@Test public void test_2043() { checkNotSubtype("{{null f2} f1}","{{any f2} f1}"); }
	@Test public void test_2044() { checkNotSubtype("{{null f2} f1}","{{any f1} f2}"); }
	@Test public void test_2045() { checkNotSubtype("{{null f2} f1}","{{any f2} f2}"); }
	@Test public void test_2046() { checkNotSubtype("{{null f2} f1}","{{null f1} f1}"); }
	@Test public void test_2047() { checkIsSubtype("{{null f2} f1}","{{null f2} f1}"); }
	@Test public void test_2048() { checkNotSubtype("{{null f2} f1}","{{null f1} f2}"); }
	@Test public void test_2049() { checkNotSubtype("{{null f2} f1}","{{null f2} f2}"); }
	@Test public void test_2050() { checkNotSubtype("{{null f2} f1}","{{int f1} f1}"); }
	@Test public void test_2051() { checkNotSubtype("{{null f2} f1}","{{int f2} f1}"); }
	@Test public void test_2052() { checkNotSubtype("{{null f2} f1}","{{int f1} f2}"); }
	@Test public void test_2053() { checkNotSubtype("{{null f2} f1}","{{int f2} f2}"); }
	@Test public void test_2054() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_2055() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test public void test_2056() { checkNotSubtype("{{null f2} f1}","int"); }
	@Test public void test_2057() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_2058() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_2059() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_2060() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_2061() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test public void test_2062() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_2063() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test public void test_2064() { checkNotSubtype("{{null f2} f1}","null|int"); }
	@Test public void test_2065() { checkNotSubtype("{{null f2} f1}","int"); }
	@Test public void test_2066() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_2067() { checkNotSubtype("{{null f2} f1}","int|null"); }
	@Test public void test_2068() { checkNotSubtype("{{null f2} f1}","int"); }
	@Test public void test_2069() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_2070() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_2071() { checkNotSubtype("{{null f2} f1}","{null f1}|null"); }
	@Test public void test_2072() { checkNotSubtype("{{null f2} f1}","{null f2}|null"); }
	@Test public void test_2073() { checkNotSubtype("{{null f2} f1}","{int f1}|int"); }
	@Test public void test_2074() { checkNotSubtype("{{null f2} f1}","{int f2}|int"); }
	@Test public void test_2075() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_2076() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test public void test_2077() { checkNotSubtype("{{null f1} f2}","int"); }
	@Test public void test_2078() { checkNotSubtype("{{null f1} f2}","{any f1}"); }
	@Test public void test_2079() { checkNotSubtype("{{null f1} f2}","{any f2}"); }
	@Test public void test_2080() { checkNotSubtype("{{null f1} f2}","{null f1}"); }
	@Test public void test_2081() { checkNotSubtype("{{null f1} f2}","{null f2}"); }
	@Test public void test_2082() { checkNotSubtype("{{null f1} f2}","{int f1}"); }
	@Test public void test_2083() { checkNotSubtype("{{null f1} f2}","{int f2}"); }
	@Test public void test_2084() { checkNotSubtype("{{null f1} f2}","(any,any)"); }
	@Test public void test_2085() { checkNotSubtype("{{null f1} f2}","(any,null)"); }
	@Test public void test_2086() { checkNotSubtype("{{null f1} f2}","(any,int)"); }
	@Test public void test_2087() { checkNotSubtype("{{null f1} f2}","(null,any)"); }
	@Test public void test_2088() { checkNotSubtype("{{null f1} f2}","(null,null)"); }
	@Test public void test_2089() { checkNotSubtype("{{null f1} f2}","(null,int)"); }
	@Test public void test_2090() { checkNotSubtype("{{null f1} f2}","(int,any)"); }
	@Test public void test_2091() { checkNotSubtype("{{null f1} f2}","(int,null)"); }
	@Test public void test_2092() { checkNotSubtype("{{null f1} f2}","(int,int)"); }
	@Test public void test_2093() { checkNotSubtype("{{null f1} f2}","({any f1},any)"); }
	@Test public void test_2094() { checkNotSubtype("{{null f1} f2}","({any f2},any)"); }
	@Test public void test_2095() { checkNotSubtype("{{null f1} f2}","({null f1},null)"); }
	@Test public void test_2096() { checkNotSubtype("{{null f1} f2}","({null f2},null)"); }
	@Test public void test_2097() { checkNotSubtype("{{null f1} f2}","({int f1},int)"); }
	@Test public void test_2098() { checkNotSubtype("{{null f1} f2}","({int f2},int)"); }
	@Test public void test_2099() { checkIsSubtype("{{null f1} f2}","{{void f1} f1}"); }
	@Test public void test_2100() { checkIsSubtype("{{null f1} f2}","{{void f2} f1}"); }
	@Test public void test_2101() { checkIsSubtype("{{null f1} f2}","{{void f1} f2}"); }
	@Test public void test_2102() { checkIsSubtype("{{null f1} f2}","{{void f2} f2}"); }
	@Test public void test_2103() { checkNotSubtype("{{null f1} f2}","{{any f1} f1}"); }
	@Test public void test_2104() { checkNotSubtype("{{null f1} f2}","{{any f2} f1}"); }
	@Test public void test_2105() { checkNotSubtype("{{null f1} f2}","{{any f1} f2}"); }
	@Test public void test_2106() { checkNotSubtype("{{null f1} f2}","{{any f2} f2}"); }
	@Test public void test_2107() { checkNotSubtype("{{null f1} f2}","{{null f1} f1}"); }
	@Test public void test_2108() { checkNotSubtype("{{null f1} f2}","{{null f2} f1}"); }
	@Test public void test_2109() { checkIsSubtype("{{null f1} f2}","{{null f1} f2}"); }
	@Test public void test_2110() { checkNotSubtype("{{null f1} f2}","{{null f2} f2}"); }
	@Test public void test_2111() { checkNotSubtype("{{null f1} f2}","{{int f1} f1}"); }
	@Test public void test_2112() { checkNotSubtype("{{null f1} f2}","{{int f2} f1}"); }
	@Test public void test_2113() { checkNotSubtype("{{null f1} f2}","{{int f1} f2}"); }
	@Test public void test_2114() { checkNotSubtype("{{null f1} f2}","{{int f2} f2}"); }
	@Test public void test_2115() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_2116() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test public void test_2117() { checkNotSubtype("{{null f1} f2}","int"); }
	@Test public void test_2118() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_2119() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_2120() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_2121() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_2122() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test public void test_2123() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_2124() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test public void test_2125() { checkNotSubtype("{{null f1} f2}","null|int"); }
	@Test public void test_2126() { checkNotSubtype("{{null f1} f2}","int"); }
	@Test public void test_2127() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_2128() { checkNotSubtype("{{null f1} f2}","int|null"); }
	@Test public void test_2129() { checkNotSubtype("{{null f1} f2}","int"); }
	@Test public void test_2130() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_2131() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_2132() { checkNotSubtype("{{null f1} f2}","{null f1}|null"); }
	@Test public void test_2133() { checkNotSubtype("{{null f1} f2}","{null f2}|null"); }
	@Test public void test_2134() { checkNotSubtype("{{null f1} f2}","{int f1}|int"); }
	@Test public void test_2135() { checkNotSubtype("{{null f1} f2}","{int f2}|int"); }
	@Test public void test_2136() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_2137() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test public void test_2138() { checkNotSubtype("{{null f2} f2}","int"); }
	@Test public void test_2139() { checkNotSubtype("{{null f2} f2}","{any f1}"); }
	@Test public void test_2140() { checkNotSubtype("{{null f2} f2}","{any f2}"); }
	@Test public void test_2141() { checkNotSubtype("{{null f2} f2}","{null f1}"); }
	@Test public void test_2142() { checkNotSubtype("{{null f2} f2}","{null f2}"); }
	@Test public void test_2143() { checkNotSubtype("{{null f2} f2}","{int f1}"); }
	@Test public void test_2144() { checkNotSubtype("{{null f2} f2}","{int f2}"); }
	@Test public void test_2145() { checkNotSubtype("{{null f2} f2}","(any,any)"); }
	@Test public void test_2146() { checkNotSubtype("{{null f2} f2}","(any,null)"); }
	@Test public void test_2147() { checkNotSubtype("{{null f2} f2}","(any,int)"); }
	@Test public void test_2148() { checkNotSubtype("{{null f2} f2}","(null,any)"); }
	@Test public void test_2149() { checkNotSubtype("{{null f2} f2}","(null,null)"); }
	@Test public void test_2150() { checkNotSubtype("{{null f2} f2}","(null,int)"); }
	@Test public void test_2151() { checkNotSubtype("{{null f2} f2}","(int,any)"); }
	@Test public void test_2152() { checkNotSubtype("{{null f2} f2}","(int,null)"); }
	@Test public void test_2153() { checkNotSubtype("{{null f2} f2}","(int,int)"); }
	@Test public void test_2154() { checkNotSubtype("{{null f2} f2}","({any f1},any)"); }
	@Test public void test_2155() { checkNotSubtype("{{null f2} f2}","({any f2},any)"); }
	@Test public void test_2156() { checkNotSubtype("{{null f2} f2}","({null f1},null)"); }
	@Test public void test_2157() { checkNotSubtype("{{null f2} f2}","({null f2},null)"); }
	@Test public void test_2158() { checkNotSubtype("{{null f2} f2}","({int f1},int)"); }
	@Test public void test_2159() { checkNotSubtype("{{null f2} f2}","({int f2},int)"); }
	@Test public void test_2160() { checkIsSubtype("{{null f2} f2}","{{void f1} f1}"); }
	@Test public void test_2161() { checkIsSubtype("{{null f2} f2}","{{void f2} f1}"); }
	@Test public void test_2162() { checkIsSubtype("{{null f2} f2}","{{void f1} f2}"); }
	@Test public void test_2163() { checkIsSubtype("{{null f2} f2}","{{void f2} f2}"); }
	@Test public void test_2164() { checkNotSubtype("{{null f2} f2}","{{any f1} f1}"); }
	@Test public void test_2165() { checkNotSubtype("{{null f2} f2}","{{any f2} f1}"); }
	@Test public void test_2166() { checkNotSubtype("{{null f2} f2}","{{any f1} f2}"); }
	@Test public void test_2167() { checkNotSubtype("{{null f2} f2}","{{any f2} f2}"); }
	@Test public void test_2168() { checkNotSubtype("{{null f2} f2}","{{null f1} f1}"); }
	@Test public void test_2169() { checkNotSubtype("{{null f2} f2}","{{null f2} f1}"); }
	@Test public void test_2170() { checkNotSubtype("{{null f2} f2}","{{null f1} f2}"); }
	@Test public void test_2171() { checkIsSubtype("{{null f2} f2}","{{null f2} f2}"); }
	@Test public void test_2172() { checkNotSubtype("{{null f2} f2}","{{int f1} f1}"); }
	@Test public void test_2173() { checkNotSubtype("{{null f2} f2}","{{int f2} f1}"); }
	@Test public void test_2174() { checkNotSubtype("{{null f2} f2}","{{int f1} f2}"); }
	@Test public void test_2175() { checkNotSubtype("{{null f2} f2}","{{int f2} f2}"); }
	@Test public void test_2176() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_2177() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test public void test_2178() { checkNotSubtype("{{null f2} f2}","int"); }
	@Test public void test_2179() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_2180() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_2181() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_2182() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_2183() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test public void test_2184() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_2185() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test public void test_2186() { checkNotSubtype("{{null f2} f2}","null|int"); }
	@Test public void test_2187() { checkNotSubtype("{{null f2} f2}","int"); }
	@Test public void test_2188() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_2189() { checkNotSubtype("{{null f2} f2}","int|null"); }
	@Test public void test_2190() { checkNotSubtype("{{null f2} f2}","int"); }
	@Test public void test_2191() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_2192() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_2193() { checkNotSubtype("{{null f2} f2}","{null f1}|null"); }
	@Test public void test_2194() { checkNotSubtype("{{null f2} f2}","{null f2}|null"); }
	@Test public void test_2195() { checkNotSubtype("{{null f2} f2}","{int f1}|int"); }
	@Test public void test_2196() { checkNotSubtype("{{null f2} f2}","{int f2}|int"); }
	@Test public void test_2197() { checkNotSubtype("{{int f1} f1}","any"); }
	@Test public void test_2198() { checkNotSubtype("{{int f1} f1}","null"); }
	@Test public void test_2199() { checkNotSubtype("{{int f1} f1}","int"); }
	@Test public void test_2200() { checkNotSubtype("{{int f1} f1}","{any f1}"); }
	@Test public void test_2201() { checkNotSubtype("{{int f1} f1}","{any f2}"); }
	@Test public void test_2202() { checkNotSubtype("{{int f1} f1}","{null f1}"); }
	@Test public void test_2203() { checkNotSubtype("{{int f1} f1}","{null f2}"); }
	@Test public void test_2204() { checkNotSubtype("{{int f1} f1}","{int f1}"); }
	@Test public void test_2205() { checkNotSubtype("{{int f1} f1}","{int f2}"); }
	@Test public void test_2206() { checkNotSubtype("{{int f1} f1}","(any,any)"); }
	@Test public void test_2207() { checkNotSubtype("{{int f1} f1}","(any,null)"); }
	@Test public void test_2208() { checkNotSubtype("{{int f1} f1}","(any,int)"); }
	@Test public void test_2209() { checkNotSubtype("{{int f1} f1}","(null,any)"); }
	@Test public void test_2210() { checkNotSubtype("{{int f1} f1}","(null,null)"); }
	@Test public void test_2211() { checkNotSubtype("{{int f1} f1}","(null,int)"); }
	@Test public void test_2212() { checkNotSubtype("{{int f1} f1}","(int,any)"); }
	@Test public void test_2213() { checkNotSubtype("{{int f1} f1}","(int,null)"); }
	@Test public void test_2214() { checkNotSubtype("{{int f1} f1}","(int,int)"); }
	@Test public void test_2215() { checkNotSubtype("{{int f1} f1}","({any f1},any)"); }
	@Test public void test_2216() { checkNotSubtype("{{int f1} f1}","({any f2},any)"); }
	@Test public void test_2217() { checkNotSubtype("{{int f1} f1}","({null f1},null)"); }
	@Test public void test_2218() { checkNotSubtype("{{int f1} f1}","({null f2},null)"); }
	@Test public void test_2219() { checkNotSubtype("{{int f1} f1}","({int f1},int)"); }
	@Test public void test_2220() { checkNotSubtype("{{int f1} f1}","({int f2},int)"); }
	@Test public void test_2221() { checkIsSubtype("{{int f1} f1}","{{void f1} f1}"); }
	@Test public void test_2222() { checkIsSubtype("{{int f1} f1}","{{void f2} f1}"); }
	@Test public void test_2223() { checkIsSubtype("{{int f1} f1}","{{void f1} f2}"); }
	@Test public void test_2224() { checkIsSubtype("{{int f1} f1}","{{void f2} f2}"); }
	@Test public void test_2225() { checkNotSubtype("{{int f1} f1}","{{any f1} f1}"); }
	@Test public void test_2226() { checkNotSubtype("{{int f1} f1}","{{any f2} f1}"); }
	@Test public void test_2227() { checkNotSubtype("{{int f1} f1}","{{any f1} f2}"); }
	@Test public void test_2228() { checkNotSubtype("{{int f1} f1}","{{any f2} f2}"); }
	@Test public void test_2229() { checkNotSubtype("{{int f1} f1}","{{null f1} f1}"); }
	@Test public void test_2230() { checkNotSubtype("{{int f1} f1}","{{null f2} f1}"); }
	@Test public void test_2231() { checkNotSubtype("{{int f1} f1}","{{null f1} f2}"); }
	@Test public void test_2232() { checkNotSubtype("{{int f1} f1}","{{null f2} f2}"); }
	@Test public void test_2233() { checkIsSubtype("{{int f1} f1}","{{int f1} f1}"); }
	@Test public void test_2234() { checkNotSubtype("{{int f1} f1}","{{int f2} f1}"); }
	@Test public void test_2235() { checkNotSubtype("{{int f1} f1}","{{int f1} f2}"); }
	@Test public void test_2236() { checkNotSubtype("{{int f1} f1}","{{int f2} f2}"); }
	@Test public void test_2237() { checkNotSubtype("{{int f1} f1}","any"); }
	@Test public void test_2238() { checkNotSubtype("{{int f1} f1}","null"); }
	@Test public void test_2239() { checkNotSubtype("{{int f1} f1}","int"); }
	@Test public void test_2240() { checkNotSubtype("{{int f1} f1}","any"); }
	@Test public void test_2241() { checkNotSubtype("{{int f1} f1}","any"); }
	@Test public void test_2242() { checkNotSubtype("{{int f1} f1}","any"); }
	@Test public void test_2243() { checkNotSubtype("{{int f1} f1}","any"); }
	@Test public void test_2244() { checkNotSubtype("{{int f1} f1}","null"); }
	@Test public void test_2245() { checkNotSubtype("{{int f1} f1}","any"); }
	@Test public void test_2246() { checkNotSubtype("{{int f1} f1}","null"); }
	@Test public void test_2247() { checkNotSubtype("{{int f1} f1}","null|int"); }
	@Test public void test_2248() { checkNotSubtype("{{int f1} f1}","int"); }
	@Test public void test_2249() { checkNotSubtype("{{int f1} f1}","any"); }
	@Test public void test_2250() { checkNotSubtype("{{int f1} f1}","int|null"); }
	@Test public void test_2251() { checkNotSubtype("{{int f1} f1}","int"); }
	@Test public void test_2252() { checkNotSubtype("{{int f1} f1}","any"); }
	@Test public void test_2253() { checkNotSubtype("{{int f1} f1}","any"); }
	@Test public void test_2254() { checkNotSubtype("{{int f1} f1}","{null f1}|null"); }
	@Test public void test_2255() { checkNotSubtype("{{int f1} f1}","{null f2}|null"); }
	@Test public void test_2256() { checkNotSubtype("{{int f1} f1}","{int f1}|int"); }
	@Test public void test_2257() { checkNotSubtype("{{int f1} f1}","{int f2}|int"); }
	@Test public void test_2258() { checkNotSubtype("{{int f2} f1}","any"); }
	@Test public void test_2259() { checkNotSubtype("{{int f2} f1}","null"); }
	@Test public void test_2260() { checkNotSubtype("{{int f2} f1}","int"); }
	@Test public void test_2261() { checkNotSubtype("{{int f2} f1}","{any f1}"); }
	@Test public void test_2262() { checkNotSubtype("{{int f2} f1}","{any f2}"); }
	@Test public void test_2263() { checkNotSubtype("{{int f2} f1}","{null f1}"); }
	@Test public void test_2264() { checkNotSubtype("{{int f2} f1}","{null f2}"); }
	@Test public void test_2265() { checkNotSubtype("{{int f2} f1}","{int f1}"); }
	@Test public void test_2266() { checkNotSubtype("{{int f2} f1}","{int f2}"); }
	@Test public void test_2267() { checkNotSubtype("{{int f2} f1}","(any,any)"); }
	@Test public void test_2268() { checkNotSubtype("{{int f2} f1}","(any,null)"); }
	@Test public void test_2269() { checkNotSubtype("{{int f2} f1}","(any,int)"); }
	@Test public void test_2270() { checkNotSubtype("{{int f2} f1}","(null,any)"); }
	@Test public void test_2271() { checkNotSubtype("{{int f2} f1}","(null,null)"); }
	@Test public void test_2272() { checkNotSubtype("{{int f2} f1}","(null,int)"); }
	@Test public void test_2273() { checkNotSubtype("{{int f2} f1}","(int,any)"); }
	@Test public void test_2274() { checkNotSubtype("{{int f2} f1}","(int,null)"); }
	@Test public void test_2275() { checkNotSubtype("{{int f2} f1}","(int,int)"); }
	@Test public void test_2276() { checkNotSubtype("{{int f2} f1}","({any f1},any)"); }
	@Test public void test_2277() { checkNotSubtype("{{int f2} f1}","({any f2},any)"); }
	@Test public void test_2278() { checkNotSubtype("{{int f2} f1}","({null f1},null)"); }
	@Test public void test_2279() { checkNotSubtype("{{int f2} f1}","({null f2},null)"); }
	@Test public void test_2280() { checkNotSubtype("{{int f2} f1}","({int f1},int)"); }
	@Test public void test_2281() { checkNotSubtype("{{int f2} f1}","({int f2},int)"); }
	@Test public void test_2282() { checkIsSubtype("{{int f2} f1}","{{void f1} f1}"); }
	@Test public void test_2283() { checkIsSubtype("{{int f2} f1}","{{void f2} f1}"); }
	@Test public void test_2284() { checkIsSubtype("{{int f2} f1}","{{void f1} f2}"); }
	@Test public void test_2285() { checkIsSubtype("{{int f2} f1}","{{void f2} f2}"); }
	@Test public void test_2286() { checkNotSubtype("{{int f2} f1}","{{any f1} f1}"); }
	@Test public void test_2287() { checkNotSubtype("{{int f2} f1}","{{any f2} f1}"); }
	@Test public void test_2288() { checkNotSubtype("{{int f2} f1}","{{any f1} f2}"); }
	@Test public void test_2289() { checkNotSubtype("{{int f2} f1}","{{any f2} f2}"); }
	@Test public void test_2290() { checkNotSubtype("{{int f2} f1}","{{null f1} f1}"); }
	@Test public void test_2291() { checkNotSubtype("{{int f2} f1}","{{null f2} f1}"); }
	@Test public void test_2292() { checkNotSubtype("{{int f2} f1}","{{null f1} f2}"); }
	@Test public void test_2293() { checkNotSubtype("{{int f2} f1}","{{null f2} f2}"); }
	@Test public void test_2294() { checkNotSubtype("{{int f2} f1}","{{int f1} f1}"); }
	@Test public void test_2295() { checkIsSubtype("{{int f2} f1}","{{int f2} f1}"); }
	@Test public void test_2296() { checkNotSubtype("{{int f2} f1}","{{int f1} f2}"); }
	@Test public void test_2297() { checkNotSubtype("{{int f2} f1}","{{int f2} f2}"); }
	@Test public void test_2298() { checkNotSubtype("{{int f2} f1}","any"); }
	@Test public void test_2299() { checkNotSubtype("{{int f2} f1}","null"); }
	@Test public void test_2300() { checkNotSubtype("{{int f2} f1}","int"); }
	@Test public void test_2301() { checkNotSubtype("{{int f2} f1}","any"); }
	@Test public void test_2302() { checkNotSubtype("{{int f2} f1}","any"); }
	@Test public void test_2303() { checkNotSubtype("{{int f2} f1}","any"); }
	@Test public void test_2304() { checkNotSubtype("{{int f2} f1}","any"); }
	@Test public void test_2305() { checkNotSubtype("{{int f2} f1}","null"); }
	@Test public void test_2306() { checkNotSubtype("{{int f2} f1}","any"); }
	@Test public void test_2307() { checkNotSubtype("{{int f2} f1}","null"); }
	@Test public void test_2308() { checkNotSubtype("{{int f2} f1}","null|int"); }
	@Test public void test_2309() { checkNotSubtype("{{int f2} f1}","int"); }
	@Test public void test_2310() { checkNotSubtype("{{int f2} f1}","any"); }
	@Test public void test_2311() { checkNotSubtype("{{int f2} f1}","int|null"); }
	@Test public void test_2312() { checkNotSubtype("{{int f2} f1}","int"); }
	@Test public void test_2313() { checkNotSubtype("{{int f2} f1}","any"); }
	@Test public void test_2314() { checkNotSubtype("{{int f2} f1}","any"); }
	@Test public void test_2315() { checkNotSubtype("{{int f2} f1}","{null f1}|null"); }
	@Test public void test_2316() { checkNotSubtype("{{int f2} f1}","{null f2}|null"); }
	@Test public void test_2317() { checkNotSubtype("{{int f2} f1}","{int f1}|int"); }
	@Test public void test_2318() { checkNotSubtype("{{int f2} f1}","{int f2}|int"); }
	@Test public void test_2319() { checkNotSubtype("{{int f1} f2}","any"); }
	@Test public void test_2320() { checkNotSubtype("{{int f1} f2}","null"); }
	@Test public void test_2321() { checkNotSubtype("{{int f1} f2}","int"); }
	@Test public void test_2322() { checkNotSubtype("{{int f1} f2}","{any f1}"); }
	@Test public void test_2323() { checkNotSubtype("{{int f1} f2}","{any f2}"); }
	@Test public void test_2324() { checkNotSubtype("{{int f1} f2}","{null f1}"); }
	@Test public void test_2325() { checkNotSubtype("{{int f1} f2}","{null f2}"); }
	@Test public void test_2326() { checkNotSubtype("{{int f1} f2}","{int f1}"); }
	@Test public void test_2327() { checkNotSubtype("{{int f1} f2}","{int f2}"); }
	@Test public void test_2328() { checkNotSubtype("{{int f1} f2}","(any,any)"); }
	@Test public void test_2329() { checkNotSubtype("{{int f1} f2}","(any,null)"); }
	@Test public void test_2330() { checkNotSubtype("{{int f1} f2}","(any,int)"); }
	@Test public void test_2331() { checkNotSubtype("{{int f1} f2}","(null,any)"); }
	@Test public void test_2332() { checkNotSubtype("{{int f1} f2}","(null,null)"); }
	@Test public void test_2333() { checkNotSubtype("{{int f1} f2}","(null,int)"); }
	@Test public void test_2334() { checkNotSubtype("{{int f1} f2}","(int,any)"); }
	@Test public void test_2335() { checkNotSubtype("{{int f1} f2}","(int,null)"); }
	@Test public void test_2336() { checkNotSubtype("{{int f1} f2}","(int,int)"); }
	@Test public void test_2337() { checkNotSubtype("{{int f1} f2}","({any f1},any)"); }
	@Test public void test_2338() { checkNotSubtype("{{int f1} f2}","({any f2},any)"); }
	@Test public void test_2339() { checkNotSubtype("{{int f1} f2}","({null f1},null)"); }
	@Test public void test_2340() { checkNotSubtype("{{int f1} f2}","({null f2},null)"); }
	@Test public void test_2341() { checkNotSubtype("{{int f1} f2}","({int f1},int)"); }
	@Test public void test_2342() { checkNotSubtype("{{int f1} f2}","({int f2},int)"); }
	@Test public void test_2343() { checkIsSubtype("{{int f1} f2}","{{void f1} f1}"); }
	@Test public void test_2344() { checkIsSubtype("{{int f1} f2}","{{void f2} f1}"); }
	@Test public void test_2345() { checkIsSubtype("{{int f1} f2}","{{void f1} f2}"); }
	@Test public void test_2346() { checkIsSubtype("{{int f1} f2}","{{void f2} f2}"); }
	@Test public void test_2347() { checkNotSubtype("{{int f1} f2}","{{any f1} f1}"); }
	@Test public void test_2348() { checkNotSubtype("{{int f1} f2}","{{any f2} f1}"); }
	@Test public void test_2349() { checkNotSubtype("{{int f1} f2}","{{any f1} f2}"); }
	@Test public void test_2350() { checkNotSubtype("{{int f1} f2}","{{any f2} f2}"); }
	@Test public void test_2351() { checkNotSubtype("{{int f1} f2}","{{null f1} f1}"); }
	@Test public void test_2352() { checkNotSubtype("{{int f1} f2}","{{null f2} f1}"); }
	@Test public void test_2353() { checkNotSubtype("{{int f1} f2}","{{null f1} f2}"); }
	@Test public void test_2354() { checkNotSubtype("{{int f1} f2}","{{null f2} f2}"); }
	@Test public void test_2355() { checkNotSubtype("{{int f1} f2}","{{int f1} f1}"); }
	@Test public void test_2356() { checkNotSubtype("{{int f1} f2}","{{int f2} f1}"); }
	@Test public void test_2357() { checkIsSubtype("{{int f1} f2}","{{int f1} f2}"); }
	@Test public void test_2358() { checkNotSubtype("{{int f1} f2}","{{int f2} f2}"); }
	@Test public void test_2359() { checkNotSubtype("{{int f1} f2}","any"); }
	@Test public void test_2360() { checkNotSubtype("{{int f1} f2}","null"); }
	@Test public void test_2361() { checkNotSubtype("{{int f1} f2}","int"); }
	@Test public void test_2362() { checkNotSubtype("{{int f1} f2}","any"); }
	@Test public void test_2363() { checkNotSubtype("{{int f1} f2}","any"); }
	@Test public void test_2364() { checkNotSubtype("{{int f1} f2}","any"); }
	@Test public void test_2365() { checkNotSubtype("{{int f1} f2}","any"); }
	@Test public void test_2366() { checkNotSubtype("{{int f1} f2}","null"); }
	@Test public void test_2367() { checkNotSubtype("{{int f1} f2}","any"); }
	@Test public void test_2368() { checkNotSubtype("{{int f1} f2}","null"); }
	@Test public void test_2369() { checkNotSubtype("{{int f1} f2}","null|int"); }
	@Test public void test_2370() { checkNotSubtype("{{int f1} f2}","int"); }
	@Test public void test_2371() { checkNotSubtype("{{int f1} f2}","any"); }
	@Test public void test_2372() { checkNotSubtype("{{int f1} f2}","int|null"); }
	@Test public void test_2373() { checkNotSubtype("{{int f1} f2}","int"); }
	@Test public void test_2374() { checkNotSubtype("{{int f1} f2}","any"); }
	@Test public void test_2375() { checkNotSubtype("{{int f1} f2}","any"); }
	@Test public void test_2376() { checkNotSubtype("{{int f1} f2}","{null f1}|null"); }
	@Test public void test_2377() { checkNotSubtype("{{int f1} f2}","{null f2}|null"); }
	@Test public void test_2378() { checkNotSubtype("{{int f1} f2}","{int f1}|int"); }
	@Test public void test_2379() { checkNotSubtype("{{int f1} f2}","{int f2}|int"); }
	@Test public void test_2380() { checkNotSubtype("{{int f2} f2}","any"); }
	@Test public void test_2381() { checkNotSubtype("{{int f2} f2}","null"); }
	@Test public void test_2382() { checkNotSubtype("{{int f2} f2}","int"); }
	@Test public void test_2383() { checkNotSubtype("{{int f2} f2}","{any f1}"); }
	@Test public void test_2384() { checkNotSubtype("{{int f2} f2}","{any f2}"); }
	@Test public void test_2385() { checkNotSubtype("{{int f2} f2}","{null f1}"); }
	@Test public void test_2386() { checkNotSubtype("{{int f2} f2}","{null f2}"); }
	@Test public void test_2387() { checkNotSubtype("{{int f2} f2}","{int f1}"); }
	@Test public void test_2388() { checkNotSubtype("{{int f2} f2}","{int f2}"); }
	@Test public void test_2389() { checkNotSubtype("{{int f2} f2}","(any,any)"); }
	@Test public void test_2390() { checkNotSubtype("{{int f2} f2}","(any,null)"); }
	@Test public void test_2391() { checkNotSubtype("{{int f2} f2}","(any,int)"); }
	@Test public void test_2392() { checkNotSubtype("{{int f2} f2}","(null,any)"); }
	@Test public void test_2393() { checkNotSubtype("{{int f2} f2}","(null,null)"); }
	@Test public void test_2394() { checkNotSubtype("{{int f2} f2}","(null,int)"); }
	@Test public void test_2395() { checkNotSubtype("{{int f2} f2}","(int,any)"); }
	@Test public void test_2396() { checkNotSubtype("{{int f2} f2}","(int,null)"); }
	@Test public void test_2397() { checkNotSubtype("{{int f2} f2}","(int,int)"); }
	@Test public void test_2398() { checkNotSubtype("{{int f2} f2}","({any f1},any)"); }
	@Test public void test_2399() { checkNotSubtype("{{int f2} f2}","({any f2},any)"); }
	@Test public void test_2400() { checkNotSubtype("{{int f2} f2}","({null f1},null)"); }
	@Test public void test_2401() { checkNotSubtype("{{int f2} f2}","({null f2},null)"); }
	@Test public void test_2402() { checkNotSubtype("{{int f2} f2}","({int f1},int)"); }
	@Test public void test_2403() { checkNotSubtype("{{int f2} f2}","({int f2},int)"); }
	@Test public void test_2404() { checkIsSubtype("{{int f2} f2}","{{void f1} f1}"); }
	@Test public void test_2405() { checkIsSubtype("{{int f2} f2}","{{void f2} f1}"); }
	@Test public void test_2406() { checkIsSubtype("{{int f2} f2}","{{void f1} f2}"); }
	@Test public void test_2407() { checkIsSubtype("{{int f2} f2}","{{void f2} f2}"); }
	@Test public void test_2408() { checkNotSubtype("{{int f2} f2}","{{any f1} f1}"); }
	@Test public void test_2409() { checkNotSubtype("{{int f2} f2}","{{any f2} f1}"); }
	@Test public void test_2410() { checkNotSubtype("{{int f2} f2}","{{any f1} f2}"); }
	@Test public void test_2411() { checkNotSubtype("{{int f2} f2}","{{any f2} f2}"); }
	@Test public void test_2412() { checkNotSubtype("{{int f2} f2}","{{null f1} f1}"); }
	@Test public void test_2413() { checkNotSubtype("{{int f2} f2}","{{null f2} f1}"); }
	@Test public void test_2414() { checkNotSubtype("{{int f2} f2}","{{null f1} f2}"); }
	@Test public void test_2415() { checkNotSubtype("{{int f2} f2}","{{null f2} f2}"); }
	@Test public void test_2416() { checkNotSubtype("{{int f2} f2}","{{int f1} f1}"); }
	@Test public void test_2417() { checkNotSubtype("{{int f2} f2}","{{int f2} f1}"); }
	@Test public void test_2418() { checkNotSubtype("{{int f2} f2}","{{int f1} f2}"); }
	@Test public void test_2419() { checkIsSubtype("{{int f2} f2}","{{int f2} f2}"); }
	@Test public void test_2420() { checkNotSubtype("{{int f2} f2}","any"); }
	@Test public void test_2421() { checkNotSubtype("{{int f2} f2}","null"); }
	@Test public void test_2422() { checkNotSubtype("{{int f2} f2}","int"); }
	@Test public void test_2423() { checkNotSubtype("{{int f2} f2}","any"); }
	@Test public void test_2424() { checkNotSubtype("{{int f2} f2}","any"); }
	@Test public void test_2425() { checkNotSubtype("{{int f2} f2}","any"); }
	@Test public void test_2426() { checkNotSubtype("{{int f2} f2}","any"); }
	@Test public void test_2427() { checkNotSubtype("{{int f2} f2}","null"); }
	@Test public void test_2428() { checkNotSubtype("{{int f2} f2}","any"); }
	@Test public void test_2429() { checkNotSubtype("{{int f2} f2}","null"); }
	@Test public void test_2430() { checkNotSubtype("{{int f2} f2}","null|int"); }
	@Test public void test_2431() { checkNotSubtype("{{int f2} f2}","int"); }
	@Test public void test_2432() { checkNotSubtype("{{int f2} f2}","any"); }
	@Test public void test_2433() { checkNotSubtype("{{int f2} f2}","int|null"); }
	@Test public void test_2434() { checkNotSubtype("{{int f2} f2}","int"); }
	@Test public void test_2435() { checkNotSubtype("{{int f2} f2}","any"); }
	@Test public void test_2436() { checkNotSubtype("{{int f2} f2}","any"); }
	@Test public void test_2437() { checkNotSubtype("{{int f2} f2}","{null f1}|null"); }
	@Test public void test_2438() { checkNotSubtype("{{int f2} f2}","{null f2}|null"); }
	@Test public void test_2439() { checkNotSubtype("{{int f2} f2}","{int f1}|int"); }
	@Test public void test_2440() { checkNotSubtype("{{int f2} f2}","{int f2}|int"); }
	@Test public void test_2441() { checkIsSubtype("any","any"); }
	@Test public void test_2442() { checkIsSubtype("any","null"); }
	@Test public void test_2443() { checkIsSubtype("any","int"); }
	@Test public void test_2444() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_2445() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_2446() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_2447() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_2448() { checkIsSubtype("any","{int f1}"); }
	@Test public void test_2449() { checkIsSubtype("any","{int f2}"); }
	@Test public void test_2450() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_2451() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_2452() { checkIsSubtype("any","(any,int)"); }
	@Test public void test_2453() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_2454() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_2455() { checkIsSubtype("any","(null,int)"); }
	@Test public void test_2456() { checkIsSubtype("any","(int,any)"); }
	@Test public void test_2457() { checkIsSubtype("any","(int,null)"); }
	@Test public void test_2458() { checkIsSubtype("any","(int,int)"); }
	@Test public void test_2459() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_2460() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_2461() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_2462() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_2463() { checkIsSubtype("any","({int f1},int)"); }
	@Test public void test_2464() { checkIsSubtype("any","({int f2},int)"); }
	@Test public void test_2465() { checkIsSubtype("any","{{void f1} f1}"); }
	@Test public void test_2466() { checkIsSubtype("any","{{void f2} f1}"); }
	@Test public void test_2467() { checkIsSubtype("any","{{void f1} f2}"); }
	@Test public void test_2468() { checkIsSubtype("any","{{void f2} f2}"); }
	@Test public void test_2469() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_2470() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_2471() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_2472() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_2473() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_2474() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_2475() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_2476() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_2477() { checkIsSubtype("any","{{int f1} f1}"); }
	@Test public void test_2478() { checkIsSubtype("any","{{int f2} f1}"); }
	@Test public void test_2479() { checkIsSubtype("any","{{int f1} f2}"); }
	@Test public void test_2480() { checkIsSubtype("any","{{int f2} f2}"); }
	@Test public void test_2481() { checkIsSubtype("any","any"); }
	@Test public void test_2482() { checkIsSubtype("any","null"); }
	@Test public void test_2483() { checkIsSubtype("any","int"); }
	@Test public void test_2484() { checkIsSubtype("any","any"); }
	@Test public void test_2485() { checkIsSubtype("any","any"); }
	@Test public void test_2486() { checkIsSubtype("any","any"); }
	@Test public void test_2487() { checkIsSubtype("any","any"); }
	@Test public void test_2488() { checkIsSubtype("any","null"); }
	@Test public void test_2489() { checkIsSubtype("any","any"); }
	@Test public void test_2490() { checkIsSubtype("any","null"); }
	@Test public void test_2491() { checkIsSubtype("any","null|int"); }
	@Test public void test_2492() { checkIsSubtype("any","int"); }
	@Test public void test_2493() { checkIsSubtype("any","any"); }
	@Test public void test_2494() { checkIsSubtype("any","int|null"); }
	@Test public void test_2495() { checkIsSubtype("any","int"); }
	@Test public void test_2496() { checkIsSubtype("any","any"); }
	@Test public void test_2497() { checkIsSubtype("any","any"); }
	@Test public void test_2498() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_2499() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_2500() { checkIsSubtype("any","{int f1}|int"); }
	@Test public void test_2501() { checkIsSubtype("any","{int f2}|int"); }
	@Test public void test_2502() { checkNotSubtype("null","any"); }
	@Test public void test_2503() { checkIsSubtype("null","null"); }
	@Test public void test_2504() { checkNotSubtype("null","int"); }
	@Test public void test_2505() { checkNotSubtype("null","{any f1}"); }
	@Test public void test_2506() { checkNotSubtype("null","{any f2}"); }
	@Test public void test_2507() { checkNotSubtype("null","{null f1}"); }
	@Test public void test_2508() { checkNotSubtype("null","{null f2}"); }
	@Test public void test_2509() { checkNotSubtype("null","{int f1}"); }
	@Test public void test_2510() { checkNotSubtype("null","{int f2}"); }
	@Test public void test_2511() { checkNotSubtype("null","(any,any)"); }
	@Test public void test_2512() { checkNotSubtype("null","(any,null)"); }
	@Test public void test_2513() { checkNotSubtype("null","(any,int)"); }
	@Test public void test_2514() { checkNotSubtype("null","(null,any)"); }
	@Test public void test_2515() { checkNotSubtype("null","(null,null)"); }
	@Test public void test_2516() { checkNotSubtype("null","(null,int)"); }
	@Test public void test_2517() { checkNotSubtype("null","(int,any)"); }
	@Test public void test_2518() { checkNotSubtype("null","(int,null)"); }
	@Test public void test_2519() { checkNotSubtype("null","(int,int)"); }
	@Test public void test_2520() { checkNotSubtype("null","({any f1},any)"); }
	@Test public void test_2521() { checkNotSubtype("null","({any f2},any)"); }
	@Test public void test_2522() { checkNotSubtype("null","({null f1},null)"); }
	@Test public void test_2523() { checkNotSubtype("null","({null f2},null)"); }
	@Test public void test_2524() { checkNotSubtype("null","({int f1},int)"); }
	@Test public void test_2525() { checkNotSubtype("null","({int f2},int)"); }
	@Test public void test_2526() { checkIsSubtype("null","{{void f1} f1}"); }
	@Test public void test_2527() { checkIsSubtype("null","{{void f2} f1}"); }
	@Test public void test_2528() { checkIsSubtype("null","{{void f1} f2}"); }
	@Test public void test_2529() { checkIsSubtype("null","{{void f2} f2}"); }
	@Test public void test_2530() { checkNotSubtype("null","{{any f1} f1}"); }
	@Test public void test_2531() { checkNotSubtype("null","{{any f2} f1}"); }
	@Test public void test_2532() { checkNotSubtype("null","{{any f1} f2}"); }
	@Test public void test_2533() { checkNotSubtype("null","{{any f2} f2}"); }
	@Test public void test_2534() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test public void test_2535() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test public void test_2536() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test public void test_2537() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test public void test_2538() { checkNotSubtype("null","{{int f1} f1}"); }
	@Test public void test_2539() { checkNotSubtype("null","{{int f2} f1}"); }
	@Test public void test_2540() { checkNotSubtype("null","{{int f1} f2}"); }
	@Test public void test_2541() { checkNotSubtype("null","{{int f2} f2}"); }
	@Test public void test_2542() { checkNotSubtype("null","any"); }
	@Test public void test_2543() { checkIsSubtype("null","null"); }
	@Test public void test_2544() { checkNotSubtype("null","int"); }
	@Test public void test_2545() { checkNotSubtype("null","any"); }
	@Test public void test_2546() { checkNotSubtype("null","any"); }
	@Test public void test_2547() { checkNotSubtype("null","any"); }
	@Test public void test_2548() { checkNotSubtype("null","any"); }
	@Test public void test_2549() { checkIsSubtype("null","null"); }
	@Test public void test_2550() { checkNotSubtype("null","any"); }
	@Test public void test_2551() { checkIsSubtype("null","null"); }
	@Test public void test_2552() { checkNotSubtype("null","null|int"); }
	@Test public void test_2553() { checkNotSubtype("null","int"); }
	@Test public void test_2554() { checkNotSubtype("null","any"); }
	@Test public void test_2555() { checkNotSubtype("null","int|null"); }
	@Test public void test_2556() { checkNotSubtype("null","int"); }
	@Test public void test_2557() { checkNotSubtype("null","any"); }
	@Test public void test_2558() { checkNotSubtype("null","any"); }
	@Test public void test_2559() { checkNotSubtype("null","{null f1}|null"); }
	@Test public void test_2560() { checkNotSubtype("null","{null f2}|null"); }
	@Test public void test_2561() { checkNotSubtype("null","{int f1}|int"); }
	@Test public void test_2562() { checkNotSubtype("null","{int f2}|int"); }
	@Test public void test_2563() { checkNotSubtype("int","any"); }
	@Test public void test_2564() { checkNotSubtype("int","null"); }
	@Test public void test_2565() { checkIsSubtype("int","int"); }
	@Test public void test_2566() { checkNotSubtype("int","{any f1}"); }
	@Test public void test_2567() { checkNotSubtype("int","{any f2}"); }
	@Test public void test_2568() { checkNotSubtype("int","{null f1}"); }
	@Test public void test_2569() { checkNotSubtype("int","{null f2}"); }
	@Test public void test_2570() { checkNotSubtype("int","{int f1}"); }
	@Test public void test_2571() { checkNotSubtype("int","{int f2}"); }
	@Test public void test_2572() { checkNotSubtype("int","(any,any)"); }
	@Test public void test_2573() { checkNotSubtype("int","(any,null)"); }
	@Test public void test_2574() { checkNotSubtype("int","(any,int)"); }
	@Test public void test_2575() { checkNotSubtype("int","(null,any)"); }
	@Test public void test_2576() { checkNotSubtype("int","(null,null)"); }
	@Test public void test_2577() { checkNotSubtype("int","(null,int)"); }
	@Test public void test_2578() { checkNotSubtype("int","(int,any)"); }
	@Test public void test_2579() { checkNotSubtype("int","(int,null)"); }
	@Test public void test_2580() { checkNotSubtype("int","(int,int)"); }
	@Test public void test_2581() { checkNotSubtype("int","({any f1},any)"); }
	@Test public void test_2582() { checkNotSubtype("int","({any f2},any)"); }
	@Test public void test_2583() { checkNotSubtype("int","({null f1},null)"); }
	@Test public void test_2584() { checkNotSubtype("int","({null f2},null)"); }
	@Test public void test_2585() { checkNotSubtype("int","({int f1},int)"); }
	@Test public void test_2586() { checkNotSubtype("int","({int f2},int)"); }
	@Test public void test_2587() { checkIsSubtype("int","{{void f1} f1}"); }
	@Test public void test_2588() { checkIsSubtype("int","{{void f2} f1}"); }
	@Test public void test_2589() { checkIsSubtype("int","{{void f1} f2}"); }
	@Test public void test_2590() { checkIsSubtype("int","{{void f2} f2}"); }
	@Test public void test_2591() { checkNotSubtype("int","{{any f1} f1}"); }
	@Test public void test_2592() { checkNotSubtype("int","{{any f2} f1}"); }
	@Test public void test_2593() { checkNotSubtype("int","{{any f1} f2}"); }
	@Test public void test_2594() { checkNotSubtype("int","{{any f2} f2}"); }
	@Test public void test_2595() { checkNotSubtype("int","{{null f1} f1}"); }
	@Test public void test_2596() { checkNotSubtype("int","{{null f2} f1}"); }
	@Test public void test_2597() { checkNotSubtype("int","{{null f1} f2}"); }
	@Test public void test_2598() { checkNotSubtype("int","{{null f2} f2}"); }
	@Test public void test_2599() { checkNotSubtype("int","{{int f1} f1}"); }
	@Test public void test_2600() { checkNotSubtype("int","{{int f2} f1}"); }
	@Test public void test_2601() { checkNotSubtype("int","{{int f1} f2}"); }
	@Test public void test_2602() { checkNotSubtype("int","{{int f2} f2}"); }
	@Test public void test_2603() { checkNotSubtype("int","any"); }
	@Test public void test_2604() { checkNotSubtype("int","null"); }
	@Test public void test_2605() { checkIsSubtype("int","int"); }
	@Test public void test_2606() { checkNotSubtype("int","any"); }
	@Test public void test_2607() { checkNotSubtype("int","any"); }
	@Test public void test_2608() { checkNotSubtype("int","any"); }
	@Test public void test_2609() { checkNotSubtype("int","any"); }
	@Test public void test_2610() { checkNotSubtype("int","null"); }
	@Test public void test_2611() { checkNotSubtype("int","any"); }
	@Test public void test_2612() { checkNotSubtype("int","null"); }
	@Test public void test_2613() { checkNotSubtype("int","null|int"); }
	@Test public void test_2614() { checkIsSubtype("int","int"); }
	@Test public void test_2615() { checkNotSubtype("int","any"); }
	@Test public void test_2616() { checkNotSubtype("int","int|null"); }
	@Test public void test_2617() { checkIsSubtype("int","int"); }
	@Test public void test_2618() { checkNotSubtype("int","any"); }
	@Test public void test_2619() { checkNotSubtype("int","any"); }
	@Test public void test_2620() { checkNotSubtype("int","{null f1}|null"); }
	@Test public void test_2621() { checkNotSubtype("int","{null f2}|null"); }
	@Test public void test_2622() { checkNotSubtype("int","{int f1}|int"); }
	@Test public void test_2623() { checkNotSubtype("int","{int f2}|int"); }
	@Test public void test_2624() { checkIsSubtype("any","any"); }
	@Test public void test_2625() { checkIsSubtype("any","null"); }
	@Test public void test_2626() { checkIsSubtype("any","int"); }
	@Test public void test_2627() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_2628() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_2629() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_2630() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_2631() { checkIsSubtype("any","{int f1}"); }
	@Test public void test_2632() { checkIsSubtype("any","{int f2}"); }
	@Test public void test_2633() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_2634() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_2635() { checkIsSubtype("any","(any,int)"); }
	@Test public void test_2636() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_2637() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_2638() { checkIsSubtype("any","(null,int)"); }
	@Test public void test_2639() { checkIsSubtype("any","(int,any)"); }
	@Test public void test_2640() { checkIsSubtype("any","(int,null)"); }
	@Test public void test_2641() { checkIsSubtype("any","(int,int)"); }
	@Test public void test_2642() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_2643() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_2644() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_2645() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_2646() { checkIsSubtype("any","({int f1},int)"); }
	@Test public void test_2647() { checkIsSubtype("any","({int f2},int)"); }
	@Test public void test_2648() { checkIsSubtype("any","{{void f1} f1}"); }
	@Test public void test_2649() { checkIsSubtype("any","{{void f2} f1}"); }
	@Test public void test_2650() { checkIsSubtype("any","{{void f1} f2}"); }
	@Test public void test_2651() { checkIsSubtype("any","{{void f2} f2}"); }
	@Test public void test_2652() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_2653() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_2654() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_2655() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_2656() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_2657() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_2658() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_2659() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_2660() { checkIsSubtype("any","{{int f1} f1}"); }
	@Test public void test_2661() { checkIsSubtype("any","{{int f2} f1}"); }
	@Test public void test_2662() { checkIsSubtype("any","{{int f1} f2}"); }
	@Test public void test_2663() { checkIsSubtype("any","{{int f2} f2}"); }
	@Test public void test_2664() { checkIsSubtype("any","any"); }
	@Test public void test_2665() { checkIsSubtype("any","null"); }
	@Test public void test_2666() { checkIsSubtype("any","int"); }
	@Test public void test_2667() { checkIsSubtype("any","any"); }
	@Test public void test_2668() { checkIsSubtype("any","any"); }
	@Test public void test_2669() { checkIsSubtype("any","any"); }
	@Test public void test_2670() { checkIsSubtype("any","any"); }
	@Test public void test_2671() { checkIsSubtype("any","null"); }
	@Test public void test_2672() { checkIsSubtype("any","any"); }
	@Test public void test_2673() { checkIsSubtype("any","null"); }
	@Test public void test_2674() { checkIsSubtype("any","null|int"); }
	@Test public void test_2675() { checkIsSubtype("any","int"); }
	@Test public void test_2676() { checkIsSubtype("any","any"); }
	@Test public void test_2677() { checkIsSubtype("any","int|null"); }
	@Test public void test_2678() { checkIsSubtype("any","int"); }
	@Test public void test_2679() { checkIsSubtype("any","any"); }
	@Test public void test_2680() { checkIsSubtype("any","any"); }
	@Test public void test_2681() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_2682() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_2683() { checkIsSubtype("any","{int f1}|int"); }
	@Test public void test_2684() { checkIsSubtype("any","{int f2}|int"); }
	@Test public void test_2685() { checkIsSubtype("any","any"); }
	@Test public void test_2686() { checkIsSubtype("any","null"); }
	@Test public void test_2687() { checkIsSubtype("any","int"); }
	@Test public void test_2688() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_2689() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_2690() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_2691() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_2692() { checkIsSubtype("any","{int f1}"); }
	@Test public void test_2693() { checkIsSubtype("any","{int f2}"); }
	@Test public void test_2694() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_2695() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_2696() { checkIsSubtype("any","(any,int)"); }
	@Test public void test_2697() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_2698() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_2699() { checkIsSubtype("any","(null,int)"); }
	@Test public void test_2700() { checkIsSubtype("any","(int,any)"); }
	@Test public void test_2701() { checkIsSubtype("any","(int,null)"); }
	@Test public void test_2702() { checkIsSubtype("any","(int,int)"); }
	@Test public void test_2703() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_2704() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_2705() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_2706() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_2707() { checkIsSubtype("any","({int f1},int)"); }
	@Test public void test_2708() { checkIsSubtype("any","({int f2},int)"); }
	@Test public void test_2709() { checkIsSubtype("any","{{void f1} f1}"); }
	@Test public void test_2710() { checkIsSubtype("any","{{void f2} f1}"); }
	@Test public void test_2711() { checkIsSubtype("any","{{void f1} f2}"); }
	@Test public void test_2712() { checkIsSubtype("any","{{void f2} f2}"); }
	@Test public void test_2713() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_2714() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_2715() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_2716() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_2717() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_2718() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_2719() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_2720() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_2721() { checkIsSubtype("any","{{int f1} f1}"); }
	@Test public void test_2722() { checkIsSubtype("any","{{int f2} f1}"); }
	@Test public void test_2723() { checkIsSubtype("any","{{int f1} f2}"); }
	@Test public void test_2724() { checkIsSubtype("any","{{int f2} f2}"); }
	@Test public void test_2725() { checkIsSubtype("any","any"); }
	@Test public void test_2726() { checkIsSubtype("any","null"); }
	@Test public void test_2727() { checkIsSubtype("any","int"); }
	@Test public void test_2728() { checkIsSubtype("any","any"); }
	@Test public void test_2729() { checkIsSubtype("any","any"); }
	@Test public void test_2730() { checkIsSubtype("any","any"); }
	@Test public void test_2731() { checkIsSubtype("any","any"); }
	@Test public void test_2732() { checkIsSubtype("any","null"); }
	@Test public void test_2733() { checkIsSubtype("any","any"); }
	@Test public void test_2734() { checkIsSubtype("any","null"); }
	@Test public void test_2735() { checkIsSubtype("any","null|int"); }
	@Test public void test_2736() { checkIsSubtype("any","int"); }
	@Test public void test_2737() { checkIsSubtype("any","any"); }
	@Test public void test_2738() { checkIsSubtype("any","int|null"); }
	@Test public void test_2739() { checkIsSubtype("any","int"); }
	@Test public void test_2740() { checkIsSubtype("any","any"); }
	@Test public void test_2741() { checkIsSubtype("any","any"); }
	@Test public void test_2742() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_2743() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_2744() { checkIsSubtype("any","{int f1}|int"); }
	@Test public void test_2745() { checkIsSubtype("any","{int f2}|int"); }
	@Test public void test_2746() { checkIsSubtype("any","any"); }
	@Test public void test_2747() { checkIsSubtype("any","null"); }
	@Test public void test_2748() { checkIsSubtype("any","int"); }
	@Test public void test_2749() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_2750() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_2751() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_2752() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_2753() { checkIsSubtype("any","{int f1}"); }
	@Test public void test_2754() { checkIsSubtype("any","{int f2}"); }
	@Test public void test_2755() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_2756() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_2757() { checkIsSubtype("any","(any,int)"); }
	@Test public void test_2758() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_2759() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_2760() { checkIsSubtype("any","(null,int)"); }
	@Test public void test_2761() { checkIsSubtype("any","(int,any)"); }
	@Test public void test_2762() { checkIsSubtype("any","(int,null)"); }
	@Test public void test_2763() { checkIsSubtype("any","(int,int)"); }
	@Test public void test_2764() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_2765() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_2766() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_2767() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_2768() { checkIsSubtype("any","({int f1},int)"); }
	@Test public void test_2769() { checkIsSubtype("any","({int f2},int)"); }
	@Test public void test_2770() { checkIsSubtype("any","{{void f1} f1}"); }
	@Test public void test_2771() { checkIsSubtype("any","{{void f2} f1}"); }
	@Test public void test_2772() { checkIsSubtype("any","{{void f1} f2}"); }
	@Test public void test_2773() { checkIsSubtype("any","{{void f2} f2}"); }
	@Test public void test_2774() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_2775() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_2776() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_2777() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_2778() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_2779() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_2780() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_2781() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_2782() { checkIsSubtype("any","{{int f1} f1}"); }
	@Test public void test_2783() { checkIsSubtype("any","{{int f2} f1}"); }
	@Test public void test_2784() { checkIsSubtype("any","{{int f1} f2}"); }
	@Test public void test_2785() { checkIsSubtype("any","{{int f2} f2}"); }
	@Test public void test_2786() { checkIsSubtype("any","any"); }
	@Test public void test_2787() { checkIsSubtype("any","null"); }
	@Test public void test_2788() { checkIsSubtype("any","int"); }
	@Test public void test_2789() { checkIsSubtype("any","any"); }
	@Test public void test_2790() { checkIsSubtype("any","any"); }
	@Test public void test_2791() { checkIsSubtype("any","any"); }
	@Test public void test_2792() { checkIsSubtype("any","any"); }
	@Test public void test_2793() { checkIsSubtype("any","null"); }
	@Test public void test_2794() { checkIsSubtype("any","any"); }
	@Test public void test_2795() { checkIsSubtype("any","null"); }
	@Test public void test_2796() { checkIsSubtype("any","null|int"); }
	@Test public void test_2797() { checkIsSubtype("any","int"); }
	@Test public void test_2798() { checkIsSubtype("any","any"); }
	@Test public void test_2799() { checkIsSubtype("any","int|null"); }
	@Test public void test_2800() { checkIsSubtype("any","int"); }
	@Test public void test_2801() { checkIsSubtype("any","any"); }
	@Test public void test_2802() { checkIsSubtype("any","any"); }
	@Test public void test_2803() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_2804() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_2805() { checkIsSubtype("any","{int f1}|int"); }
	@Test public void test_2806() { checkIsSubtype("any","{int f2}|int"); }
	@Test public void test_2807() { checkIsSubtype("any","any"); }
	@Test public void test_2808() { checkIsSubtype("any","null"); }
	@Test public void test_2809() { checkIsSubtype("any","int"); }
	@Test public void test_2810() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_2811() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_2812() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_2813() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_2814() { checkIsSubtype("any","{int f1}"); }
	@Test public void test_2815() { checkIsSubtype("any","{int f2}"); }
	@Test public void test_2816() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_2817() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_2818() { checkIsSubtype("any","(any,int)"); }
	@Test public void test_2819() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_2820() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_2821() { checkIsSubtype("any","(null,int)"); }
	@Test public void test_2822() { checkIsSubtype("any","(int,any)"); }
	@Test public void test_2823() { checkIsSubtype("any","(int,null)"); }
	@Test public void test_2824() { checkIsSubtype("any","(int,int)"); }
	@Test public void test_2825() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_2826() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_2827() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_2828() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_2829() { checkIsSubtype("any","({int f1},int)"); }
	@Test public void test_2830() { checkIsSubtype("any","({int f2},int)"); }
	@Test public void test_2831() { checkIsSubtype("any","{{void f1} f1}"); }
	@Test public void test_2832() { checkIsSubtype("any","{{void f2} f1}"); }
	@Test public void test_2833() { checkIsSubtype("any","{{void f1} f2}"); }
	@Test public void test_2834() { checkIsSubtype("any","{{void f2} f2}"); }
	@Test public void test_2835() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_2836() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_2837() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_2838() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_2839() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_2840() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_2841() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_2842() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_2843() { checkIsSubtype("any","{{int f1} f1}"); }
	@Test public void test_2844() { checkIsSubtype("any","{{int f2} f1}"); }
	@Test public void test_2845() { checkIsSubtype("any","{{int f1} f2}"); }
	@Test public void test_2846() { checkIsSubtype("any","{{int f2} f2}"); }
	@Test public void test_2847() { checkIsSubtype("any","any"); }
	@Test public void test_2848() { checkIsSubtype("any","null"); }
	@Test public void test_2849() { checkIsSubtype("any","int"); }
	@Test public void test_2850() { checkIsSubtype("any","any"); }
	@Test public void test_2851() { checkIsSubtype("any","any"); }
	@Test public void test_2852() { checkIsSubtype("any","any"); }
	@Test public void test_2853() { checkIsSubtype("any","any"); }
	@Test public void test_2854() { checkIsSubtype("any","null"); }
	@Test public void test_2855() { checkIsSubtype("any","any"); }
	@Test public void test_2856() { checkIsSubtype("any","null"); }
	@Test public void test_2857() { checkIsSubtype("any","null|int"); }
	@Test public void test_2858() { checkIsSubtype("any","int"); }
	@Test public void test_2859() { checkIsSubtype("any","any"); }
	@Test public void test_2860() { checkIsSubtype("any","int|null"); }
	@Test public void test_2861() { checkIsSubtype("any","int"); }
	@Test public void test_2862() { checkIsSubtype("any","any"); }
	@Test public void test_2863() { checkIsSubtype("any","any"); }
	@Test public void test_2864() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_2865() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_2866() { checkIsSubtype("any","{int f1}|int"); }
	@Test public void test_2867() { checkIsSubtype("any","{int f2}|int"); }
	@Test public void test_2868() { checkNotSubtype("null","any"); }
	@Test public void test_2869() { checkIsSubtype("null","null"); }
	@Test public void test_2870() { checkNotSubtype("null","int"); }
	@Test public void test_2871() { checkNotSubtype("null","{any f1}"); }
	@Test public void test_2872() { checkNotSubtype("null","{any f2}"); }
	@Test public void test_2873() { checkNotSubtype("null","{null f1}"); }
	@Test public void test_2874() { checkNotSubtype("null","{null f2}"); }
	@Test public void test_2875() { checkNotSubtype("null","{int f1}"); }
	@Test public void test_2876() { checkNotSubtype("null","{int f2}"); }
	@Test public void test_2877() { checkNotSubtype("null","(any,any)"); }
	@Test public void test_2878() { checkNotSubtype("null","(any,null)"); }
	@Test public void test_2879() { checkNotSubtype("null","(any,int)"); }
	@Test public void test_2880() { checkNotSubtype("null","(null,any)"); }
	@Test public void test_2881() { checkNotSubtype("null","(null,null)"); }
	@Test public void test_2882() { checkNotSubtype("null","(null,int)"); }
	@Test public void test_2883() { checkNotSubtype("null","(int,any)"); }
	@Test public void test_2884() { checkNotSubtype("null","(int,null)"); }
	@Test public void test_2885() { checkNotSubtype("null","(int,int)"); }
	@Test public void test_2886() { checkNotSubtype("null","({any f1},any)"); }
	@Test public void test_2887() { checkNotSubtype("null","({any f2},any)"); }
	@Test public void test_2888() { checkNotSubtype("null","({null f1},null)"); }
	@Test public void test_2889() { checkNotSubtype("null","({null f2},null)"); }
	@Test public void test_2890() { checkNotSubtype("null","({int f1},int)"); }
	@Test public void test_2891() { checkNotSubtype("null","({int f2},int)"); }
	@Test public void test_2892() { checkIsSubtype("null","{{void f1} f1}"); }
	@Test public void test_2893() { checkIsSubtype("null","{{void f2} f1}"); }
	@Test public void test_2894() { checkIsSubtype("null","{{void f1} f2}"); }
	@Test public void test_2895() { checkIsSubtype("null","{{void f2} f2}"); }
	@Test public void test_2896() { checkNotSubtype("null","{{any f1} f1}"); }
	@Test public void test_2897() { checkNotSubtype("null","{{any f2} f1}"); }
	@Test public void test_2898() { checkNotSubtype("null","{{any f1} f2}"); }
	@Test public void test_2899() { checkNotSubtype("null","{{any f2} f2}"); }
	@Test public void test_2900() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test public void test_2901() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test public void test_2902() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test public void test_2903() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test public void test_2904() { checkNotSubtype("null","{{int f1} f1}"); }
	@Test public void test_2905() { checkNotSubtype("null","{{int f2} f1}"); }
	@Test public void test_2906() { checkNotSubtype("null","{{int f1} f2}"); }
	@Test public void test_2907() { checkNotSubtype("null","{{int f2} f2}"); }
	@Test public void test_2908() { checkNotSubtype("null","any"); }
	@Test public void test_2909() { checkIsSubtype("null","null"); }
	@Test public void test_2910() { checkNotSubtype("null","int"); }
	@Test public void test_2911() { checkNotSubtype("null","any"); }
	@Test public void test_2912() { checkNotSubtype("null","any"); }
	@Test public void test_2913() { checkNotSubtype("null","any"); }
	@Test public void test_2914() { checkNotSubtype("null","any"); }
	@Test public void test_2915() { checkIsSubtype("null","null"); }
	@Test public void test_2916() { checkNotSubtype("null","any"); }
	@Test public void test_2917() { checkIsSubtype("null","null"); }
	@Test public void test_2918() { checkNotSubtype("null","null|int"); }
	@Test public void test_2919() { checkNotSubtype("null","int"); }
	@Test public void test_2920() { checkNotSubtype("null","any"); }
	@Test public void test_2921() { checkNotSubtype("null","int|null"); }
	@Test public void test_2922() { checkNotSubtype("null","int"); }
	@Test public void test_2923() { checkNotSubtype("null","any"); }
	@Test public void test_2924() { checkNotSubtype("null","any"); }
	@Test public void test_2925() { checkNotSubtype("null","{null f1}|null"); }
	@Test public void test_2926() { checkNotSubtype("null","{null f2}|null"); }
	@Test public void test_2927() { checkNotSubtype("null","{int f1}|int"); }
	@Test public void test_2928() { checkNotSubtype("null","{int f2}|int"); }
	@Test public void test_2929() { checkIsSubtype("any","any"); }
	@Test public void test_2930() { checkIsSubtype("any","null"); }
	@Test public void test_2931() { checkIsSubtype("any","int"); }
	@Test public void test_2932() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_2933() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_2934() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_2935() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_2936() { checkIsSubtype("any","{int f1}"); }
	@Test public void test_2937() { checkIsSubtype("any","{int f2}"); }
	@Test public void test_2938() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_2939() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_2940() { checkIsSubtype("any","(any,int)"); }
	@Test public void test_2941() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_2942() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_2943() { checkIsSubtype("any","(null,int)"); }
	@Test public void test_2944() { checkIsSubtype("any","(int,any)"); }
	@Test public void test_2945() { checkIsSubtype("any","(int,null)"); }
	@Test public void test_2946() { checkIsSubtype("any","(int,int)"); }
	@Test public void test_2947() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_2948() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_2949() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_2950() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_2951() { checkIsSubtype("any","({int f1},int)"); }
	@Test public void test_2952() { checkIsSubtype("any","({int f2},int)"); }
	@Test public void test_2953() { checkIsSubtype("any","{{void f1} f1}"); }
	@Test public void test_2954() { checkIsSubtype("any","{{void f2} f1}"); }
	@Test public void test_2955() { checkIsSubtype("any","{{void f1} f2}"); }
	@Test public void test_2956() { checkIsSubtype("any","{{void f2} f2}"); }
	@Test public void test_2957() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_2958() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_2959() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_2960() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_2961() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_2962() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_2963() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_2964() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_2965() { checkIsSubtype("any","{{int f1} f1}"); }
	@Test public void test_2966() { checkIsSubtype("any","{{int f2} f1}"); }
	@Test public void test_2967() { checkIsSubtype("any","{{int f1} f2}"); }
	@Test public void test_2968() { checkIsSubtype("any","{{int f2} f2}"); }
	@Test public void test_2969() { checkIsSubtype("any","any"); }
	@Test public void test_2970() { checkIsSubtype("any","null"); }
	@Test public void test_2971() { checkIsSubtype("any","int"); }
	@Test public void test_2972() { checkIsSubtype("any","any"); }
	@Test public void test_2973() { checkIsSubtype("any","any"); }
	@Test public void test_2974() { checkIsSubtype("any","any"); }
	@Test public void test_2975() { checkIsSubtype("any","any"); }
	@Test public void test_2976() { checkIsSubtype("any","null"); }
	@Test public void test_2977() { checkIsSubtype("any","any"); }
	@Test public void test_2978() { checkIsSubtype("any","null"); }
	@Test public void test_2979() { checkIsSubtype("any","null|int"); }
	@Test public void test_2980() { checkIsSubtype("any","int"); }
	@Test public void test_2981() { checkIsSubtype("any","any"); }
	@Test public void test_2982() { checkIsSubtype("any","int|null"); }
	@Test public void test_2983() { checkIsSubtype("any","int"); }
	@Test public void test_2984() { checkIsSubtype("any","any"); }
	@Test public void test_2985() { checkIsSubtype("any","any"); }
	@Test public void test_2986() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_2987() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_2988() { checkIsSubtype("any","{int f1}|int"); }
	@Test public void test_2989() { checkIsSubtype("any","{int f2}|int"); }
	@Test public void test_2990() { checkNotSubtype("null","any"); }
	@Test public void test_2991() { checkIsSubtype("null","null"); }
	@Test public void test_2992() { checkNotSubtype("null","int"); }
	@Test public void test_2993() { checkNotSubtype("null","{any f1}"); }
	@Test public void test_2994() { checkNotSubtype("null","{any f2}"); }
	@Test public void test_2995() { checkNotSubtype("null","{null f1}"); }
	@Test public void test_2996() { checkNotSubtype("null","{null f2}"); }
	@Test public void test_2997() { checkNotSubtype("null","{int f1}"); }
	@Test public void test_2998() { checkNotSubtype("null","{int f2}"); }
	@Test public void test_2999() { checkNotSubtype("null","(any,any)"); }
	@Test public void test_3000() { checkNotSubtype("null","(any,null)"); }
	@Test public void test_3001() { checkNotSubtype("null","(any,int)"); }
	@Test public void test_3002() { checkNotSubtype("null","(null,any)"); }
	@Test public void test_3003() { checkNotSubtype("null","(null,null)"); }
	@Test public void test_3004() { checkNotSubtype("null","(null,int)"); }
	@Test public void test_3005() { checkNotSubtype("null","(int,any)"); }
	@Test public void test_3006() { checkNotSubtype("null","(int,null)"); }
	@Test public void test_3007() { checkNotSubtype("null","(int,int)"); }
	@Test public void test_3008() { checkNotSubtype("null","({any f1},any)"); }
	@Test public void test_3009() { checkNotSubtype("null","({any f2},any)"); }
	@Test public void test_3010() { checkNotSubtype("null","({null f1},null)"); }
	@Test public void test_3011() { checkNotSubtype("null","({null f2},null)"); }
	@Test public void test_3012() { checkNotSubtype("null","({int f1},int)"); }
	@Test public void test_3013() { checkNotSubtype("null","({int f2},int)"); }
	@Test public void test_3014() { checkIsSubtype("null","{{void f1} f1}"); }
	@Test public void test_3015() { checkIsSubtype("null","{{void f2} f1}"); }
	@Test public void test_3016() { checkIsSubtype("null","{{void f1} f2}"); }
	@Test public void test_3017() { checkIsSubtype("null","{{void f2} f2}"); }
	@Test public void test_3018() { checkNotSubtype("null","{{any f1} f1}"); }
	@Test public void test_3019() { checkNotSubtype("null","{{any f2} f1}"); }
	@Test public void test_3020() { checkNotSubtype("null","{{any f1} f2}"); }
	@Test public void test_3021() { checkNotSubtype("null","{{any f2} f2}"); }
	@Test public void test_3022() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test public void test_3023() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test public void test_3024() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test public void test_3025() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test public void test_3026() { checkNotSubtype("null","{{int f1} f1}"); }
	@Test public void test_3027() { checkNotSubtype("null","{{int f2} f1}"); }
	@Test public void test_3028() { checkNotSubtype("null","{{int f1} f2}"); }
	@Test public void test_3029() { checkNotSubtype("null","{{int f2} f2}"); }
	@Test public void test_3030() { checkNotSubtype("null","any"); }
	@Test public void test_3031() { checkIsSubtype("null","null"); }
	@Test public void test_3032() { checkNotSubtype("null","int"); }
	@Test public void test_3033() { checkNotSubtype("null","any"); }
	@Test public void test_3034() { checkNotSubtype("null","any"); }
	@Test public void test_3035() { checkNotSubtype("null","any"); }
	@Test public void test_3036() { checkNotSubtype("null","any"); }
	@Test public void test_3037() { checkIsSubtype("null","null"); }
	@Test public void test_3038() { checkNotSubtype("null","any"); }
	@Test public void test_3039() { checkIsSubtype("null","null"); }
	@Test public void test_3040() { checkNotSubtype("null","null|int"); }
	@Test public void test_3041() { checkNotSubtype("null","int"); }
	@Test public void test_3042() { checkNotSubtype("null","any"); }
	@Test public void test_3043() { checkNotSubtype("null","int|null"); }
	@Test public void test_3044() { checkNotSubtype("null","int"); }
	@Test public void test_3045() { checkNotSubtype("null","any"); }
	@Test public void test_3046() { checkNotSubtype("null","any"); }
	@Test public void test_3047() { checkNotSubtype("null","{null f1}|null"); }
	@Test public void test_3048() { checkNotSubtype("null","{null f2}|null"); }
	@Test public void test_3049() { checkNotSubtype("null","{int f1}|int"); }
	@Test public void test_3050() { checkNotSubtype("null","{int f2}|int"); }
	@Test public void test_3051() { checkNotSubtype("null|int","any"); }
	@Test public void test_3052() { checkIsSubtype("null|int","null"); }
	@Test public void test_3053() { checkIsSubtype("null|int","int"); }
	@Test public void test_3054() { checkNotSubtype("null|int","{any f1}"); }
	@Test public void test_3055() { checkNotSubtype("null|int","{any f2}"); }
	@Test public void test_3056() { checkNotSubtype("null|int","{null f1}"); }
	@Test public void test_3057() { checkNotSubtype("null|int","{null f2}"); }
	@Test public void test_3058() { checkNotSubtype("null|int","{int f1}"); }
	@Test public void test_3059() { checkNotSubtype("null|int","{int f2}"); }
	@Test public void test_3060() { checkNotSubtype("null|int","(any,any)"); }
	@Test public void test_3061() { checkNotSubtype("null|int","(any,null)"); }
	@Test public void test_3062() { checkNotSubtype("null|int","(any,int)"); }
	@Test public void test_3063() { checkNotSubtype("null|int","(null,any)"); }
	@Test public void test_3064() { checkNotSubtype("null|int","(null,null)"); }
	@Test public void test_3065() { checkNotSubtype("null|int","(null,int)"); }
	@Test public void test_3066() { checkNotSubtype("null|int","(int,any)"); }
	@Test public void test_3067() { checkNotSubtype("null|int","(int,null)"); }
	@Test public void test_3068() { checkNotSubtype("null|int","(int,int)"); }
	@Test public void test_3069() { checkNotSubtype("null|int","({any f1},any)"); }
	@Test public void test_3070() { checkNotSubtype("null|int","({any f2},any)"); }
	@Test public void test_3071() { checkNotSubtype("null|int","({null f1},null)"); }
	@Test public void test_3072() { checkNotSubtype("null|int","({null f2},null)"); }
	@Test public void test_3073() { checkNotSubtype("null|int","({int f1},int)"); }
	@Test public void test_3074() { checkNotSubtype("null|int","({int f2},int)"); }
	@Test public void test_3075() { checkIsSubtype("null|int","{{void f1} f1}"); }
	@Test public void test_3076() { checkIsSubtype("null|int","{{void f2} f1}"); }
	@Test public void test_3077() { checkIsSubtype("null|int","{{void f1} f2}"); }
	@Test public void test_3078() { checkIsSubtype("null|int","{{void f2} f2}"); }
	@Test public void test_3079() { checkNotSubtype("null|int","{{any f1} f1}"); }
	@Test public void test_3080() { checkNotSubtype("null|int","{{any f2} f1}"); }
	@Test public void test_3081() { checkNotSubtype("null|int","{{any f1} f2}"); }
	@Test public void test_3082() { checkNotSubtype("null|int","{{any f2} f2}"); }
	@Test public void test_3083() { checkNotSubtype("null|int","{{null f1} f1}"); }
	@Test public void test_3084() { checkNotSubtype("null|int","{{null f2} f1}"); }
	@Test public void test_3085() { checkNotSubtype("null|int","{{null f1} f2}"); }
	@Test public void test_3086() { checkNotSubtype("null|int","{{null f2} f2}"); }
	@Test public void test_3087() { checkNotSubtype("null|int","{{int f1} f1}"); }
	@Test public void test_3088() { checkNotSubtype("null|int","{{int f2} f1}"); }
	@Test public void test_3089() { checkNotSubtype("null|int","{{int f1} f2}"); }
	@Test public void test_3090() { checkNotSubtype("null|int","{{int f2} f2}"); }
	@Test public void test_3091() { checkNotSubtype("null|int","any"); }
	@Test public void test_3092() { checkIsSubtype("null|int","null"); }
	@Test public void test_3093() { checkIsSubtype("null|int","int"); }
	@Test public void test_3094() { checkNotSubtype("null|int","any"); }
	@Test public void test_3095() { checkNotSubtype("null|int","any"); }
	@Test public void test_3096() { checkNotSubtype("null|int","any"); }
	@Test public void test_3097() { checkNotSubtype("null|int","any"); }
	@Test public void test_3098() { checkIsSubtype("null|int","null"); }
	@Test public void test_3099() { checkNotSubtype("null|int","any"); }
	@Test public void test_3100() { checkIsSubtype("null|int","null"); }
	@Test public void test_3101() { checkIsSubtype("null|int","null|int"); }
	@Test public void test_3102() { checkIsSubtype("null|int","int"); }
	@Test public void test_3103() { checkNotSubtype("null|int","any"); }
	@Test public void test_3104() { checkIsSubtype("null|int","int|null"); }
	@Test public void test_3105() { checkIsSubtype("null|int","int"); }
	@Test public void test_3106() { checkNotSubtype("null|int","any"); }
	@Test public void test_3107() { checkNotSubtype("null|int","any"); }
	@Test public void test_3108() { checkNotSubtype("null|int","{null f1}|null"); }
	@Test public void test_3109() { checkNotSubtype("null|int","{null f2}|null"); }
	@Test public void test_3110() { checkNotSubtype("null|int","{int f1}|int"); }
	@Test public void test_3111() { checkNotSubtype("null|int","{int f2}|int"); }
	@Test public void test_3112() { checkNotSubtype("int","any"); }
	@Test public void test_3113() { checkNotSubtype("int","null"); }
	@Test public void test_3114() { checkIsSubtype("int","int"); }
	@Test public void test_3115() { checkNotSubtype("int","{any f1}"); }
	@Test public void test_3116() { checkNotSubtype("int","{any f2}"); }
	@Test public void test_3117() { checkNotSubtype("int","{null f1}"); }
	@Test public void test_3118() { checkNotSubtype("int","{null f2}"); }
	@Test public void test_3119() { checkNotSubtype("int","{int f1}"); }
	@Test public void test_3120() { checkNotSubtype("int","{int f2}"); }
	@Test public void test_3121() { checkNotSubtype("int","(any,any)"); }
	@Test public void test_3122() { checkNotSubtype("int","(any,null)"); }
	@Test public void test_3123() { checkNotSubtype("int","(any,int)"); }
	@Test public void test_3124() { checkNotSubtype("int","(null,any)"); }
	@Test public void test_3125() { checkNotSubtype("int","(null,null)"); }
	@Test public void test_3126() { checkNotSubtype("int","(null,int)"); }
	@Test public void test_3127() { checkNotSubtype("int","(int,any)"); }
	@Test public void test_3128() { checkNotSubtype("int","(int,null)"); }
	@Test public void test_3129() { checkNotSubtype("int","(int,int)"); }
	@Test public void test_3130() { checkNotSubtype("int","({any f1},any)"); }
	@Test public void test_3131() { checkNotSubtype("int","({any f2},any)"); }
	@Test public void test_3132() { checkNotSubtype("int","({null f1},null)"); }
	@Test public void test_3133() { checkNotSubtype("int","({null f2},null)"); }
	@Test public void test_3134() { checkNotSubtype("int","({int f1},int)"); }
	@Test public void test_3135() { checkNotSubtype("int","({int f2},int)"); }
	@Test public void test_3136() { checkIsSubtype("int","{{void f1} f1}"); }
	@Test public void test_3137() { checkIsSubtype("int","{{void f2} f1}"); }
	@Test public void test_3138() { checkIsSubtype("int","{{void f1} f2}"); }
	@Test public void test_3139() { checkIsSubtype("int","{{void f2} f2}"); }
	@Test public void test_3140() { checkNotSubtype("int","{{any f1} f1}"); }
	@Test public void test_3141() { checkNotSubtype("int","{{any f2} f1}"); }
	@Test public void test_3142() { checkNotSubtype("int","{{any f1} f2}"); }
	@Test public void test_3143() { checkNotSubtype("int","{{any f2} f2}"); }
	@Test public void test_3144() { checkNotSubtype("int","{{null f1} f1}"); }
	@Test public void test_3145() { checkNotSubtype("int","{{null f2} f1}"); }
	@Test public void test_3146() { checkNotSubtype("int","{{null f1} f2}"); }
	@Test public void test_3147() { checkNotSubtype("int","{{null f2} f2}"); }
	@Test public void test_3148() { checkNotSubtype("int","{{int f1} f1}"); }
	@Test public void test_3149() { checkNotSubtype("int","{{int f2} f1}"); }
	@Test public void test_3150() { checkNotSubtype("int","{{int f1} f2}"); }
	@Test public void test_3151() { checkNotSubtype("int","{{int f2} f2}"); }
	@Test public void test_3152() { checkNotSubtype("int","any"); }
	@Test public void test_3153() { checkNotSubtype("int","null"); }
	@Test public void test_3154() { checkIsSubtype("int","int"); }
	@Test public void test_3155() { checkNotSubtype("int","any"); }
	@Test public void test_3156() { checkNotSubtype("int","any"); }
	@Test public void test_3157() { checkNotSubtype("int","any"); }
	@Test public void test_3158() { checkNotSubtype("int","any"); }
	@Test public void test_3159() { checkNotSubtype("int","null"); }
	@Test public void test_3160() { checkNotSubtype("int","any"); }
	@Test public void test_3161() { checkNotSubtype("int","null"); }
	@Test public void test_3162() { checkNotSubtype("int","null|int"); }
	@Test public void test_3163() { checkIsSubtype("int","int"); }
	@Test public void test_3164() { checkNotSubtype("int","any"); }
	@Test public void test_3165() { checkNotSubtype("int","int|null"); }
	@Test public void test_3166() { checkIsSubtype("int","int"); }
	@Test public void test_3167() { checkNotSubtype("int","any"); }
	@Test public void test_3168() { checkNotSubtype("int","any"); }
	@Test public void test_3169() { checkNotSubtype("int","{null f1}|null"); }
	@Test public void test_3170() { checkNotSubtype("int","{null f2}|null"); }
	@Test public void test_3171() { checkNotSubtype("int","{int f1}|int"); }
	@Test public void test_3172() { checkNotSubtype("int","{int f2}|int"); }
	@Test public void test_3173() { checkIsSubtype("any","any"); }
	@Test public void test_3174() { checkIsSubtype("any","null"); }
	@Test public void test_3175() { checkIsSubtype("any","int"); }
	@Test public void test_3176() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_3177() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_3178() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_3179() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_3180() { checkIsSubtype("any","{int f1}"); }
	@Test public void test_3181() { checkIsSubtype("any","{int f2}"); }
	@Test public void test_3182() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_3183() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_3184() { checkIsSubtype("any","(any,int)"); }
	@Test public void test_3185() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_3186() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_3187() { checkIsSubtype("any","(null,int)"); }
	@Test public void test_3188() { checkIsSubtype("any","(int,any)"); }
	@Test public void test_3189() { checkIsSubtype("any","(int,null)"); }
	@Test public void test_3190() { checkIsSubtype("any","(int,int)"); }
	@Test public void test_3191() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_3192() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_3193() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_3194() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_3195() { checkIsSubtype("any","({int f1},int)"); }
	@Test public void test_3196() { checkIsSubtype("any","({int f2},int)"); }
	@Test public void test_3197() { checkIsSubtype("any","{{void f1} f1}"); }
	@Test public void test_3198() { checkIsSubtype("any","{{void f2} f1}"); }
	@Test public void test_3199() { checkIsSubtype("any","{{void f1} f2}"); }
	@Test public void test_3200() { checkIsSubtype("any","{{void f2} f2}"); }
	@Test public void test_3201() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_3202() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_3203() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_3204() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_3205() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_3206() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_3207() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_3208() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_3209() { checkIsSubtype("any","{{int f1} f1}"); }
	@Test public void test_3210() { checkIsSubtype("any","{{int f2} f1}"); }
	@Test public void test_3211() { checkIsSubtype("any","{{int f1} f2}"); }
	@Test public void test_3212() { checkIsSubtype("any","{{int f2} f2}"); }
	@Test public void test_3213() { checkIsSubtype("any","any"); }
	@Test public void test_3214() { checkIsSubtype("any","null"); }
	@Test public void test_3215() { checkIsSubtype("any","int"); }
	@Test public void test_3216() { checkIsSubtype("any","any"); }
	@Test public void test_3217() { checkIsSubtype("any","any"); }
	@Test public void test_3218() { checkIsSubtype("any","any"); }
	@Test public void test_3219() { checkIsSubtype("any","any"); }
	@Test public void test_3220() { checkIsSubtype("any","null"); }
	@Test public void test_3221() { checkIsSubtype("any","any"); }
	@Test public void test_3222() { checkIsSubtype("any","null"); }
	@Test public void test_3223() { checkIsSubtype("any","null|int"); }
	@Test public void test_3224() { checkIsSubtype("any","int"); }
	@Test public void test_3225() { checkIsSubtype("any","any"); }
	@Test public void test_3226() { checkIsSubtype("any","int|null"); }
	@Test public void test_3227() { checkIsSubtype("any","int"); }
	@Test public void test_3228() { checkIsSubtype("any","any"); }
	@Test public void test_3229() { checkIsSubtype("any","any"); }
	@Test public void test_3230() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_3231() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_3232() { checkIsSubtype("any","{int f1}|int"); }
	@Test public void test_3233() { checkIsSubtype("any","{int f2}|int"); }
	@Test public void test_3234() { checkNotSubtype("int|null","any"); }
	@Test public void test_3235() { checkIsSubtype("int|null","null"); }
	@Test public void test_3236() { checkIsSubtype("int|null","int"); }
	@Test public void test_3237() { checkNotSubtype("int|null","{any f1}"); }
	@Test public void test_3238() { checkNotSubtype("int|null","{any f2}"); }
	@Test public void test_3239() { checkNotSubtype("int|null","{null f1}"); }
	@Test public void test_3240() { checkNotSubtype("int|null","{null f2}"); }
	@Test public void test_3241() { checkNotSubtype("int|null","{int f1}"); }
	@Test public void test_3242() { checkNotSubtype("int|null","{int f2}"); }
	@Test public void test_3243() { checkNotSubtype("int|null","(any,any)"); }
	@Test public void test_3244() { checkNotSubtype("int|null","(any,null)"); }
	@Test public void test_3245() { checkNotSubtype("int|null","(any,int)"); }
	@Test public void test_3246() { checkNotSubtype("int|null","(null,any)"); }
	@Test public void test_3247() { checkNotSubtype("int|null","(null,null)"); }
	@Test public void test_3248() { checkNotSubtype("int|null","(null,int)"); }
	@Test public void test_3249() { checkNotSubtype("int|null","(int,any)"); }
	@Test public void test_3250() { checkNotSubtype("int|null","(int,null)"); }
	@Test public void test_3251() { checkNotSubtype("int|null","(int,int)"); }
	@Test public void test_3252() { checkNotSubtype("int|null","({any f1},any)"); }
	@Test public void test_3253() { checkNotSubtype("int|null","({any f2},any)"); }
	@Test public void test_3254() { checkNotSubtype("int|null","({null f1},null)"); }
	@Test public void test_3255() { checkNotSubtype("int|null","({null f2},null)"); }
	@Test public void test_3256() { checkNotSubtype("int|null","({int f1},int)"); }
	@Test public void test_3257() { checkNotSubtype("int|null","({int f2},int)"); }
	@Test public void test_3258() { checkIsSubtype("int|null","{{void f1} f1}"); }
	@Test public void test_3259() { checkIsSubtype("int|null","{{void f2} f1}"); }
	@Test public void test_3260() { checkIsSubtype("int|null","{{void f1} f2}"); }
	@Test public void test_3261() { checkIsSubtype("int|null","{{void f2} f2}"); }
	@Test public void test_3262() { checkNotSubtype("int|null","{{any f1} f1}"); }
	@Test public void test_3263() { checkNotSubtype("int|null","{{any f2} f1}"); }
	@Test public void test_3264() { checkNotSubtype("int|null","{{any f1} f2}"); }
	@Test public void test_3265() { checkNotSubtype("int|null","{{any f2} f2}"); }
	@Test public void test_3266() { checkNotSubtype("int|null","{{null f1} f1}"); }
	@Test public void test_3267() { checkNotSubtype("int|null","{{null f2} f1}"); }
	@Test public void test_3268() { checkNotSubtype("int|null","{{null f1} f2}"); }
	@Test public void test_3269() { checkNotSubtype("int|null","{{null f2} f2}"); }
	@Test public void test_3270() { checkNotSubtype("int|null","{{int f1} f1}"); }
	@Test public void test_3271() { checkNotSubtype("int|null","{{int f2} f1}"); }
	@Test public void test_3272() { checkNotSubtype("int|null","{{int f1} f2}"); }
	@Test public void test_3273() { checkNotSubtype("int|null","{{int f2} f2}"); }
	@Test public void test_3274() { checkNotSubtype("int|null","any"); }
	@Test public void test_3275() { checkIsSubtype("int|null","null"); }
	@Test public void test_3276() { checkIsSubtype("int|null","int"); }
	@Test public void test_3277() { checkNotSubtype("int|null","any"); }
	@Test public void test_3278() { checkNotSubtype("int|null","any"); }
	@Test public void test_3279() { checkNotSubtype("int|null","any"); }
	@Test public void test_3280() { checkNotSubtype("int|null","any"); }
	@Test public void test_3281() { checkIsSubtype("int|null","null"); }
	@Test public void test_3282() { checkNotSubtype("int|null","any"); }
	@Test public void test_3283() { checkIsSubtype("int|null","null"); }
	@Test public void test_3284() { checkIsSubtype("int|null","null|int"); }
	@Test public void test_3285() { checkIsSubtype("int|null","int"); }
	@Test public void test_3286() { checkNotSubtype("int|null","any"); }
	@Test public void test_3287() { checkIsSubtype("int|null","int|null"); }
	@Test public void test_3288() { checkIsSubtype("int|null","int"); }
	@Test public void test_3289() { checkNotSubtype("int|null","any"); }
	@Test public void test_3290() { checkNotSubtype("int|null","any"); }
	@Test public void test_3291() { checkNotSubtype("int|null","{null f1}|null"); }
	@Test public void test_3292() { checkNotSubtype("int|null","{null f2}|null"); }
	@Test public void test_3293() { checkNotSubtype("int|null","{int f1}|int"); }
	@Test public void test_3294() { checkNotSubtype("int|null","{int f2}|int"); }
	@Test public void test_3295() { checkNotSubtype("int","any"); }
	@Test public void test_3296() { checkNotSubtype("int","null"); }
	@Test public void test_3297() { checkIsSubtype("int","int"); }
	@Test public void test_3298() { checkNotSubtype("int","{any f1}"); }
	@Test public void test_3299() { checkNotSubtype("int","{any f2}"); }
	@Test public void test_3300() { checkNotSubtype("int","{null f1}"); }
	@Test public void test_3301() { checkNotSubtype("int","{null f2}"); }
	@Test public void test_3302() { checkNotSubtype("int","{int f1}"); }
	@Test public void test_3303() { checkNotSubtype("int","{int f2}"); }
	@Test public void test_3304() { checkNotSubtype("int","(any,any)"); }
	@Test public void test_3305() { checkNotSubtype("int","(any,null)"); }
	@Test public void test_3306() { checkNotSubtype("int","(any,int)"); }
	@Test public void test_3307() { checkNotSubtype("int","(null,any)"); }
	@Test public void test_3308() { checkNotSubtype("int","(null,null)"); }
	@Test public void test_3309() { checkNotSubtype("int","(null,int)"); }
	@Test public void test_3310() { checkNotSubtype("int","(int,any)"); }
	@Test public void test_3311() { checkNotSubtype("int","(int,null)"); }
	@Test public void test_3312() { checkNotSubtype("int","(int,int)"); }
	@Test public void test_3313() { checkNotSubtype("int","({any f1},any)"); }
	@Test public void test_3314() { checkNotSubtype("int","({any f2},any)"); }
	@Test public void test_3315() { checkNotSubtype("int","({null f1},null)"); }
	@Test public void test_3316() { checkNotSubtype("int","({null f2},null)"); }
	@Test public void test_3317() { checkNotSubtype("int","({int f1},int)"); }
	@Test public void test_3318() { checkNotSubtype("int","({int f2},int)"); }
	@Test public void test_3319() { checkIsSubtype("int","{{void f1} f1}"); }
	@Test public void test_3320() { checkIsSubtype("int","{{void f2} f1}"); }
	@Test public void test_3321() { checkIsSubtype("int","{{void f1} f2}"); }
	@Test public void test_3322() { checkIsSubtype("int","{{void f2} f2}"); }
	@Test public void test_3323() { checkNotSubtype("int","{{any f1} f1}"); }
	@Test public void test_3324() { checkNotSubtype("int","{{any f2} f1}"); }
	@Test public void test_3325() { checkNotSubtype("int","{{any f1} f2}"); }
	@Test public void test_3326() { checkNotSubtype("int","{{any f2} f2}"); }
	@Test public void test_3327() { checkNotSubtype("int","{{null f1} f1}"); }
	@Test public void test_3328() { checkNotSubtype("int","{{null f2} f1}"); }
	@Test public void test_3329() { checkNotSubtype("int","{{null f1} f2}"); }
	@Test public void test_3330() { checkNotSubtype("int","{{null f2} f2}"); }
	@Test public void test_3331() { checkNotSubtype("int","{{int f1} f1}"); }
	@Test public void test_3332() { checkNotSubtype("int","{{int f2} f1}"); }
	@Test public void test_3333() { checkNotSubtype("int","{{int f1} f2}"); }
	@Test public void test_3334() { checkNotSubtype("int","{{int f2} f2}"); }
	@Test public void test_3335() { checkNotSubtype("int","any"); }
	@Test public void test_3336() { checkNotSubtype("int","null"); }
	@Test public void test_3337() { checkIsSubtype("int","int"); }
	@Test public void test_3338() { checkNotSubtype("int","any"); }
	@Test public void test_3339() { checkNotSubtype("int","any"); }
	@Test public void test_3340() { checkNotSubtype("int","any"); }
	@Test public void test_3341() { checkNotSubtype("int","any"); }
	@Test public void test_3342() { checkNotSubtype("int","null"); }
	@Test public void test_3343() { checkNotSubtype("int","any"); }
	@Test public void test_3344() { checkNotSubtype("int","null"); }
	@Test public void test_3345() { checkNotSubtype("int","null|int"); }
	@Test public void test_3346() { checkIsSubtype("int","int"); }
	@Test public void test_3347() { checkNotSubtype("int","any"); }
	@Test public void test_3348() { checkNotSubtype("int","int|null"); }
	@Test public void test_3349() { checkIsSubtype("int","int"); }
	@Test public void test_3350() { checkNotSubtype("int","any"); }
	@Test public void test_3351() { checkNotSubtype("int","any"); }
	@Test public void test_3352() { checkNotSubtype("int","{null f1}|null"); }
	@Test public void test_3353() { checkNotSubtype("int","{null f2}|null"); }
	@Test public void test_3354() { checkNotSubtype("int","{int f1}|int"); }
	@Test public void test_3355() { checkNotSubtype("int","{int f2}|int"); }
	@Test public void test_3356() { checkIsSubtype("any","any"); }
	@Test public void test_3357() { checkIsSubtype("any","null"); }
	@Test public void test_3358() { checkIsSubtype("any","int"); }
	@Test public void test_3359() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_3360() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_3361() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_3362() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_3363() { checkIsSubtype("any","{int f1}"); }
	@Test public void test_3364() { checkIsSubtype("any","{int f2}"); }
	@Test public void test_3365() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_3366() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_3367() { checkIsSubtype("any","(any,int)"); }
	@Test public void test_3368() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_3369() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_3370() { checkIsSubtype("any","(null,int)"); }
	@Test public void test_3371() { checkIsSubtype("any","(int,any)"); }
	@Test public void test_3372() { checkIsSubtype("any","(int,null)"); }
	@Test public void test_3373() { checkIsSubtype("any","(int,int)"); }
	@Test public void test_3374() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_3375() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_3376() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_3377() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_3378() { checkIsSubtype("any","({int f1},int)"); }
	@Test public void test_3379() { checkIsSubtype("any","({int f2},int)"); }
	@Test public void test_3380() { checkIsSubtype("any","{{void f1} f1}"); }
	@Test public void test_3381() { checkIsSubtype("any","{{void f2} f1}"); }
	@Test public void test_3382() { checkIsSubtype("any","{{void f1} f2}"); }
	@Test public void test_3383() { checkIsSubtype("any","{{void f2} f2}"); }
	@Test public void test_3384() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_3385() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_3386() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_3387() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_3388() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_3389() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_3390() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_3391() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_3392() { checkIsSubtype("any","{{int f1} f1}"); }
	@Test public void test_3393() { checkIsSubtype("any","{{int f2} f1}"); }
	@Test public void test_3394() { checkIsSubtype("any","{{int f1} f2}"); }
	@Test public void test_3395() { checkIsSubtype("any","{{int f2} f2}"); }
	@Test public void test_3396() { checkIsSubtype("any","any"); }
	@Test public void test_3397() { checkIsSubtype("any","null"); }
	@Test public void test_3398() { checkIsSubtype("any","int"); }
	@Test public void test_3399() { checkIsSubtype("any","any"); }
	@Test public void test_3400() { checkIsSubtype("any","any"); }
	@Test public void test_3401() { checkIsSubtype("any","any"); }
	@Test public void test_3402() { checkIsSubtype("any","any"); }
	@Test public void test_3403() { checkIsSubtype("any","null"); }
	@Test public void test_3404() { checkIsSubtype("any","any"); }
	@Test public void test_3405() { checkIsSubtype("any","null"); }
	@Test public void test_3406() { checkIsSubtype("any","null|int"); }
	@Test public void test_3407() { checkIsSubtype("any","int"); }
	@Test public void test_3408() { checkIsSubtype("any","any"); }
	@Test public void test_3409() { checkIsSubtype("any","int|null"); }
	@Test public void test_3410() { checkIsSubtype("any","int"); }
	@Test public void test_3411() { checkIsSubtype("any","any"); }
	@Test public void test_3412() { checkIsSubtype("any","any"); }
	@Test public void test_3413() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_3414() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_3415() { checkIsSubtype("any","{int f1}|int"); }
	@Test public void test_3416() { checkIsSubtype("any","{int f2}|int"); }
	@Test public void test_3417() { checkIsSubtype("any","any"); }
	@Test public void test_3418() { checkIsSubtype("any","null"); }
	@Test public void test_3419() { checkIsSubtype("any","int"); }
	@Test public void test_3420() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_3421() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_3422() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_3423() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_3424() { checkIsSubtype("any","{int f1}"); }
	@Test public void test_3425() { checkIsSubtype("any","{int f2}"); }
	@Test public void test_3426() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_3427() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_3428() { checkIsSubtype("any","(any,int)"); }
	@Test public void test_3429() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_3430() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_3431() { checkIsSubtype("any","(null,int)"); }
	@Test public void test_3432() { checkIsSubtype("any","(int,any)"); }
	@Test public void test_3433() { checkIsSubtype("any","(int,null)"); }
	@Test public void test_3434() { checkIsSubtype("any","(int,int)"); }
	@Test public void test_3435() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_3436() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_3437() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_3438() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_3439() { checkIsSubtype("any","({int f1},int)"); }
	@Test public void test_3440() { checkIsSubtype("any","({int f2},int)"); }
	@Test public void test_3441() { checkIsSubtype("any","{{void f1} f1}"); }
	@Test public void test_3442() { checkIsSubtype("any","{{void f2} f1}"); }
	@Test public void test_3443() { checkIsSubtype("any","{{void f1} f2}"); }
	@Test public void test_3444() { checkIsSubtype("any","{{void f2} f2}"); }
	@Test public void test_3445() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_3446() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_3447() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_3448() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_3449() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_3450() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_3451() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_3452() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_3453() { checkIsSubtype("any","{{int f1} f1}"); }
	@Test public void test_3454() { checkIsSubtype("any","{{int f2} f1}"); }
	@Test public void test_3455() { checkIsSubtype("any","{{int f1} f2}"); }
	@Test public void test_3456() { checkIsSubtype("any","{{int f2} f2}"); }
	@Test public void test_3457() { checkIsSubtype("any","any"); }
	@Test public void test_3458() { checkIsSubtype("any","null"); }
	@Test public void test_3459() { checkIsSubtype("any","int"); }
	@Test public void test_3460() { checkIsSubtype("any","any"); }
	@Test public void test_3461() { checkIsSubtype("any","any"); }
	@Test public void test_3462() { checkIsSubtype("any","any"); }
	@Test public void test_3463() { checkIsSubtype("any","any"); }
	@Test public void test_3464() { checkIsSubtype("any","null"); }
	@Test public void test_3465() { checkIsSubtype("any","any"); }
	@Test public void test_3466() { checkIsSubtype("any","null"); }
	@Test public void test_3467() { checkIsSubtype("any","null|int"); }
	@Test public void test_3468() { checkIsSubtype("any","int"); }
	@Test public void test_3469() { checkIsSubtype("any","any"); }
	@Test public void test_3470() { checkIsSubtype("any","int|null"); }
	@Test public void test_3471() { checkIsSubtype("any","int"); }
	@Test public void test_3472() { checkIsSubtype("any","any"); }
	@Test public void test_3473() { checkIsSubtype("any","any"); }
	@Test public void test_3474() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_3475() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_3476() { checkIsSubtype("any","{int f1}|int"); }
	@Test public void test_3477() { checkIsSubtype("any","{int f2}|int"); }
	@Test public void test_3478() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3479() { checkIsSubtype("{null f1}|null","null"); }
	@Test public void test_3480() { checkNotSubtype("{null f1}|null","int"); }
	@Test public void test_3481() { checkNotSubtype("{null f1}|null","{any f1}"); }
	@Test public void test_3482() { checkNotSubtype("{null f1}|null","{any f2}"); }
	@Test public void test_3483() { checkIsSubtype("{null f1}|null","{null f1}"); }
	@Test public void test_3484() { checkNotSubtype("{null f1}|null","{null f2}"); }
	@Test public void test_3485() { checkNotSubtype("{null f1}|null","{int f1}"); }
	@Test public void test_3486() { checkNotSubtype("{null f1}|null","{int f2}"); }
	@Test public void test_3487() { checkNotSubtype("{null f1}|null","(any,any)"); }
	@Test public void test_3488() { checkNotSubtype("{null f1}|null","(any,null)"); }
	@Test public void test_3489() { checkNotSubtype("{null f1}|null","(any,int)"); }
	@Test public void test_3490() { checkNotSubtype("{null f1}|null","(null,any)"); }
	@Test public void test_3491() { checkNotSubtype("{null f1}|null","(null,null)"); }
	@Test public void test_3492() { checkNotSubtype("{null f1}|null","(null,int)"); }
	@Test public void test_3493() { checkNotSubtype("{null f1}|null","(int,any)"); }
	@Test public void test_3494() { checkNotSubtype("{null f1}|null","(int,null)"); }
	@Test public void test_3495() { checkNotSubtype("{null f1}|null","(int,int)"); }
	@Test public void test_3496() { checkNotSubtype("{null f1}|null","({any f1},any)"); }
	@Test public void test_3497() { checkNotSubtype("{null f1}|null","({any f2},any)"); }
	@Test public void test_3498() { checkNotSubtype("{null f1}|null","({null f1},null)"); }
	@Test public void test_3499() { checkNotSubtype("{null f1}|null","({null f2},null)"); }
	@Test public void test_3500() { checkNotSubtype("{null f1}|null","({int f1},int)"); }
	@Test public void test_3501() { checkNotSubtype("{null f1}|null","({int f2},int)"); }
	@Test public void test_3502() { checkIsSubtype("{null f1}|null","{{void f1} f1}"); }
	@Test public void test_3503() { checkIsSubtype("{null f1}|null","{{void f2} f1}"); }
	@Test public void test_3504() { checkIsSubtype("{null f1}|null","{{void f1} f2}"); }
	@Test public void test_3505() { checkIsSubtype("{null f1}|null","{{void f2} f2}"); }
	@Test public void test_3506() { checkNotSubtype("{null f1}|null","{{any f1} f1}"); }
	@Test public void test_3507() { checkNotSubtype("{null f1}|null","{{any f2} f1}"); }
	@Test public void test_3508() { checkNotSubtype("{null f1}|null","{{any f1} f2}"); }
	@Test public void test_3509() { checkNotSubtype("{null f1}|null","{{any f2} f2}"); }
	@Test public void test_3510() { checkNotSubtype("{null f1}|null","{{null f1} f1}"); }
	@Test public void test_3511() { checkNotSubtype("{null f1}|null","{{null f2} f1}"); }
	@Test public void test_3512() { checkNotSubtype("{null f1}|null","{{null f1} f2}"); }
	@Test public void test_3513() { checkNotSubtype("{null f1}|null","{{null f2} f2}"); }
	@Test public void test_3514() { checkNotSubtype("{null f1}|null","{{int f1} f1}"); }
	@Test public void test_3515() { checkNotSubtype("{null f1}|null","{{int f2} f1}"); }
	@Test public void test_3516() { checkNotSubtype("{null f1}|null","{{int f1} f2}"); }
	@Test public void test_3517() { checkNotSubtype("{null f1}|null","{{int f2} f2}"); }
	@Test public void test_3518() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3519() { checkIsSubtype("{null f1}|null","null"); }
	@Test public void test_3520() { checkNotSubtype("{null f1}|null","int"); }
	@Test public void test_3521() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3522() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3523() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3524() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3525() { checkIsSubtype("{null f1}|null","null"); }
	@Test public void test_3526() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3527() { checkIsSubtype("{null f1}|null","null"); }
	@Test public void test_3528() { checkNotSubtype("{null f1}|null","null|int"); }
	@Test public void test_3529() { checkNotSubtype("{null f1}|null","int"); }
	@Test public void test_3530() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3531() { checkNotSubtype("{null f1}|null","int|null"); }
	@Test public void test_3532() { checkNotSubtype("{null f1}|null","int"); }
	@Test public void test_3533() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3534() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3535() { checkIsSubtype("{null f1}|null","{null f1}|null"); }
	@Test public void test_3536() { checkNotSubtype("{null f1}|null","{null f2}|null"); }
	@Test public void test_3537() { checkNotSubtype("{null f1}|null","{int f1}|int"); }
	@Test public void test_3538() { checkNotSubtype("{null f1}|null","{int f2}|int"); }
	@Test public void test_3539() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3540() { checkIsSubtype("{null f2}|null","null"); }
	@Test public void test_3541() { checkNotSubtype("{null f2}|null","int"); }
	@Test public void test_3542() { checkNotSubtype("{null f2}|null","{any f1}"); }
	@Test public void test_3543() { checkNotSubtype("{null f2}|null","{any f2}"); }
	@Test public void test_3544() { checkNotSubtype("{null f2}|null","{null f1}"); }
	@Test public void test_3545() { checkIsSubtype("{null f2}|null","{null f2}"); }
	@Test public void test_3546() { checkNotSubtype("{null f2}|null","{int f1}"); }
	@Test public void test_3547() { checkNotSubtype("{null f2}|null","{int f2}"); }
	@Test public void test_3548() { checkNotSubtype("{null f2}|null","(any,any)"); }
	@Test public void test_3549() { checkNotSubtype("{null f2}|null","(any,null)"); }
	@Test public void test_3550() { checkNotSubtype("{null f2}|null","(any,int)"); }
	@Test public void test_3551() { checkNotSubtype("{null f2}|null","(null,any)"); }
	@Test public void test_3552() { checkNotSubtype("{null f2}|null","(null,null)"); }
	@Test public void test_3553() { checkNotSubtype("{null f2}|null","(null,int)"); }
	@Test public void test_3554() { checkNotSubtype("{null f2}|null","(int,any)"); }
	@Test public void test_3555() { checkNotSubtype("{null f2}|null","(int,null)"); }
	@Test public void test_3556() { checkNotSubtype("{null f2}|null","(int,int)"); }
	@Test public void test_3557() { checkNotSubtype("{null f2}|null","({any f1},any)"); }
	@Test public void test_3558() { checkNotSubtype("{null f2}|null","({any f2},any)"); }
	@Test public void test_3559() { checkNotSubtype("{null f2}|null","({null f1},null)"); }
	@Test public void test_3560() { checkNotSubtype("{null f2}|null","({null f2},null)"); }
	@Test public void test_3561() { checkNotSubtype("{null f2}|null","({int f1},int)"); }
	@Test public void test_3562() { checkNotSubtype("{null f2}|null","({int f2},int)"); }
	@Test public void test_3563() { checkIsSubtype("{null f2}|null","{{void f1} f1}"); }
	@Test public void test_3564() { checkIsSubtype("{null f2}|null","{{void f2} f1}"); }
	@Test public void test_3565() { checkIsSubtype("{null f2}|null","{{void f1} f2}"); }
	@Test public void test_3566() { checkIsSubtype("{null f2}|null","{{void f2} f2}"); }
	@Test public void test_3567() { checkNotSubtype("{null f2}|null","{{any f1} f1}"); }
	@Test public void test_3568() { checkNotSubtype("{null f2}|null","{{any f2} f1}"); }
	@Test public void test_3569() { checkNotSubtype("{null f2}|null","{{any f1} f2}"); }
	@Test public void test_3570() { checkNotSubtype("{null f2}|null","{{any f2} f2}"); }
	@Test public void test_3571() { checkNotSubtype("{null f2}|null","{{null f1} f1}"); }
	@Test public void test_3572() { checkNotSubtype("{null f2}|null","{{null f2} f1}"); }
	@Test public void test_3573() { checkNotSubtype("{null f2}|null","{{null f1} f2}"); }
	@Test public void test_3574() { checkNotSubtype("{null f2}|null","{{null f2} f2}"); }
	@Test public void test_3575() { checkNotSubtype("{null f2}|null","{{int f1} f1}"); }
	@Test public void test_3576() { checkNotSubtype("{null f2}|null","{{int f2} f1}"); }
	@Test public void test_3577() { checkNotSubtype("{null f2}|null","{{int f1} f2}"); }
	@Test public void test_3578() { checkNotSubtype("{null f2}|null","{{int f2} f2}"); }
	@Test public void test_3579() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3580() { checkIsSubtype("{null f2}|null","null"); }
	@Test public void test_3581() { checkNotSubtype("{null f2}|null","int"); }
	@Test public void test_3582() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3583() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3584() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3585() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3586() { checkIsSubtype("{null f2}|null","null"); }
	@Test public void test_3587() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3588() { checkIsSubtype("{null f2}|null","null"); }
	@Test public void test_3589() { checkNotSubtype("{null f2}|null","null|int"); }
	@Test public void test_3590() { checkNotSubtype("{null f2}|null","int"); }
	@Test public void test_3591() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3592() { checkNotSubtype("{null f2}|null","int|null"); }
	@Test public void test_3593() { checkNotSubtype("{null f2}|null","int"); }
	@Test public void test_3594() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3595() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3596() { checkNotSubtype("{null f2}|null","{null f1}|null"); }
	@Test public void test_3597() { checkIsSubtype("{null f2}|null","{null f2}|null"); }
	@Test public void test_3598() { checkNotSubtype("{null f2}|null","{int f1}|int"); }
	@Test public void test_3599() { checkNotSubtype("{null f2}|null","{int f2}|int"); }
	@Test public void test_3600() { checkNotSubtype("{int f1}|int","any"); }
	@Test public void test_3601() { checkNotSubtype("{int f1}|int","null"); }
	@Test public void test_3602() { checkIsSubtype("{int f1}|int","int"); }
	@Test public void test_3603() { checkNotSubtype("{int f1}|int","{any f1}"); }
	@Test public void test_3604() { checkNotSubtype("{int f1}|int","{any f2}"); }
	@Test public void test_3605() { checkNotSubtype("{int f1}|int","{null f1}"); }
	@Test public void test_3606() { checkNotSubtype("{int f1}|int","{null f2}"); }
	@Test public void test_3607() { checkIsSubtype("{int f1}|int","{int f1}"); }
	@Test public void test_3608() { checkNotSubtype("{int f1}|int","{int f2}"); }
	@Test public void test_3609() { checkNotSubtype("{int f1}|int","(any,any)"); }
	@Test public void test_3610() { checkNotSubtype("{int f1}|int","(any,null)"); }
	@Test public void test_3611() { checkNotSubtype("{int f1}|int","(any,int)"); }
	@Test public void test_3612() { checkNotSubtype("{int f1}|int","(null,any)"); }
	@Test public void test_3613() { checkNotSubtype("{int f1}|int","(null,null)"); }
	@Test public void test_3614() { checkNotSubtype("{int f1}|int","(null,int)"); }
	@Test public void test_3615() { checkNotSubtype("{int f1}|int","(int,any)"); }
	@Test public void test_3616() { checkNotSubtype("{int f1}|int","(int,null)"); }
	@Test public void test_3617() { checkNotSubtype("{int f1}|int","(int,int)"); }
	@Test public void test_3618() { checkNotSubtype("{int f1}|int","({any f1},any)"); }
	@Test public void test_3619() { checkNotSubtype("{int f1}|int","({any f2},any)"); }
	@Test public void test_3620() { checkNotSubtype("{int f1}|int","({null f1},null)"); }
	@Test public void test_3621() { checkNotSubtype("{int f1}|int","({null f2},null)"); }
	@Test public void test_3622() { checkNotSubtype("{int f1}|int","({int f1},int)"); }
	@Test public void test_3623() { checkNotSubtype("{int f1}|int","({int f2},int)"); }
	@Test public void test_3624() { checkIsSubtype("{int f1}|int","{{void f1} f1}"); }
	@Test public void test_3625() { checkIsSubtype("{int f1}|int","{{void f2} f1}"); }
	@Test public void test_3626() { checkIsSubtype("{int f1}|int","{{void f1} f2}"); }
	@Test public void test_3627() { checkIsSubtype("{int f1}|int","{{void f2} f2}"); }
	@Test public void test_3628() { checkNotSubtype("{int f1}|int","{{any f1} f1}"); }
	@Test public void test_3629() { checkNotSubtype("{int f1}|int","{{any f2} f1}"); }
	@Test public void test_3630() { checkNotSubtype("{int f1}|int","{{any f1} f2}"); }
	@Test public void test_3631() { checkNotSubtype("{int f1}|int","{{any f2} f2}"); }
	@Test public void test_3632() { checkNotSubtype("{int f1}|int","{{null f1} f1}"); }
	@Test public void test_3633() { checkNotSubtype("{int f1}|int","{{null f2} f1}"); }
	@Test public void test_3634() { checkNotSubtype("{int f1}|int","{{null f1} f2}"); }
	@Test public void test_3635() { checkNotSubtype("{int f1}|int","{{null f2} f2}"); }
	@Test public void test_3636() { checkNotSubtype("{int f1}|int","{{int f1} f1}"); }
	@Test public void test_3637() { checkNotSubtype("{int f1}|int","{{int f2} f1}"); }
	@Test public void test_3638() { checkNotSubtype("{int f1}|int","{{int f1} f2}"); }
	@Test public void test_3639() { checkNotSubtype("{int f1}|int","{{int f2} f2}"); }
	@Test public void test_3640() { checkNotSubtype("{int f1}|int","any"); }
	@Test public void test_3641() { checkNotSubtype("{int f1}|int","null"); }
	@Test public void test_3642() { checkIsSubtype("{int f1}|int","int"); }
	@Test public void test_3643() { checkNotSubtype("{int f1}|int","any"); }
	@Test public void test_3644() { checkNotSubtype("{int f1}|int","any"); }
	@Test public void test_3645() { checkNotSubtype("{int f1}|int","any"); }
	@Test public void test_3646() { checkNotSubtype("{int f1}|int","any"); }
	@Test public void test_3647() { checkNotSubtype("{int f1}|int","null"); }
	@Test public void test_3648() { checkNotSubtype("{int f1}|int","any"); }
	@Test public void test_3649() { checkNotSubtype("{int f1}|int","null"); }
	@Test public void test_3650() { checkNotSubtype("{int f1}|int","null|int"); }
	@Test public void test_3651() { checkIsSubtype("{int f1}|int","int"); }
	@Test public void test_3652() { checkNotSubtype("{int f1}|int","any"); }
	@Test public void test_3653() { checkNotSubtype("{int f1}|int","int|null"); }
	@Test public void test_3654() { checkIsSubtype("{int f1}|int","int"); }
	@Test public void test_3655() { checkNotSubtype("{int f1}|int","any"); }
	@Test public void test_3656() { checkNotSubtype("{int f1}|int","any"); }
	@Test public void test_3657() { checkNotSubtype("{int f1}|int","{null f1}|null"); }
	@Test public void test_3658() { checkNotSubtype("{int f1}|int","{null f2}|null"); }
	@Test public void test_3659() { checkIsSubtype("{int f1}|int","{int f1}|int"); }
	@Test public void test_3660() { checkNotSubtype("{int f1}|int","{int f2}|int"); }
	@Test public void test_3661() { checkNotSubtype("{int f2}|int","any"); }
	@Test public void test_3662() { checkNotSubtype("{int f2}|int","null"); }
	@Test public void test_3663() { checkIsSubtype("{int f2}|int","int"); }
	@Test public void test_3664() { checkNotSubtype("{int f2}|int","{any f1}"); }
	@Test public void test_3665() { checkNotSubtype("{int f2}|int","{any f2}"); }
	@Test public void test_3666() { checkNotSubtype("{int f2}|int","{null f1}"); }
	@Test public void test_3667() { checkNotSubtype("{int f2}|int","{null f2}"); }
	@Test public void test_3668() { checkNotSubtype("{int f2}|int","{int f1}"); }
	@Test public void test_3669() { checkIsSubtype("{int f2}|int","{int f2}"); }
	@Test public void test_3670() { checkNotSubtype("{int f2}|int","(any,any)"); }
	@Test public void test_3671() { checkNotSubtype("{int f2}|int","(any,null)"); }
	@Test public void test_3672() { checkNotSubtype("{int f2}|int","(any,int)"); }
	@Test public void test_3673() { checkNotSubtype("{int f2}|int","(null,any)"); }
	@Test public void test_3674() { checkNotSubtype("{int f2}|int","(null,null)"); }
	@Test public void test_3675() { checkNotSubtype("{int f2}|int","(null,int)"); }
	@Test public void test_3676() { checkNotSubtype("{int f2}|int","(int,any)"); }
	@Test public void test_3677() { checkNotSubtype("{int f2}|int","(int,null)"); }
	@Test public void test_3678() { checkNotSubtype("{int f2}|int","(int,int)"); }
	@Test public void test_3679() { checkNotSubtype("{int f2}|int","({any f1},any)"); }
	@Test public void test_3680() { checkNotSubtype("{int f2}|int","({any f2},any)"); }
	@Test public void test_3681() { checkNotSubtype("{int f2}|int","({null f1},null)"); }
	@Test public void test_3682() { checkNotSubtype("{int f2}|int","({null f2},null)"); }
	@Test public void test_3683() { checkNotSubtype("{int f2}|int","({int f1},int)"); }
	@Test public void test_3684() { checkNotSubtype("{int f2}|int","({int f2},int)"); }
	@Test public void test_3685() { checkIsSubtype("{int f2}|int","{{void f1} f1}"); }
	@Test public void test_3686() { checkIsSubtype("{int f2}|int","{{void f2} f1}"); }
	@Test public void test_3687() { checkIsSubtype("{int f2}|int","{{void f1} f2}"); }
	@Test public void test_3688() { checkIsSubtype("{int f2}|int","{{void f2} f2}"); }
	@Test public void test_3689() { checkNotSubtype("{int f2}|int","{{any f1} f1}"); }
	@Test public void test_3690() { checkNotSubtype("{int f2}|int","{{any f2} f1}"); }
	@Test public void test_3691() { checkNotSubtype("{int f2}|int","{{any f1} f2}"); }
	@Test public void test_3692() { checkNotSubtype("{int f2}|int","{{any f2} f2}"); }
	@Test public void test_3693() { checkNotSubtype("{int f2}|int","{{null f1} f1}"); }
	@Test public void test_3694() { checkNotSubtype("{int f2}|int","{{null f2} f1}"); }
	@Test public void test_3695() { checkNotSubtype("{int f2}|int","{{null f1} f2}"); }
	@Test public void test_3696() { checkNotSubtype("{int f2}|int","{{null f2} f2}"); }
	@Test public void test_3697() { checkNotSubtype("{int f2}|int","{{int f1} f1}"); }
	@Test public void test_3698() { checkNotSubtype("{int f2}|int","{{int f2} f1}"); }
	@Test public void test_3699() { checkNotSubtype("{int f2}|int","{{int f1} f2}"); }
	@Test public void test_3700() { checkNotSubtype("{int f2}|int","{{int f2} f2}"); }
	@Test public void test_3701() { checkNotSubtype("{int f2}|int","any"); }
	@Test public void test_3702() { checkNotSubtype("{int f2}|int","null"); }
	@Test public void test_3703() { checkIsSubtype("{int f2}|int","int"); }
	@Test public void test_3704() { checkNotSubtype("{int f2}|int","any"); }
	@Test public void test_3705() { checkNotSubtype("{int f2}|int","any"); }
	@Test public void test_3706() { checkNotSubtype("{int f2}|int","any"); }
	@Test public void test_3707() { checkNotSubtype("{int f2}|int","any"); }
	@Test public void test_3708() { checkNotSubtype("{int f2}|int","null"); }
	@Test public void test_3709() { checkNotSubtype("{int f2}|int","any"); }
	@Test public void test_3710() { checkNotSubtype("{int f2}|int","null"); }
	@Test public void test_3711() { checkNotSubtype("{int f2}|int","null|int"); }
	@Test public void test_3712() { checkIsSubtype("{int f2}|int","int"); }
	@Test public void test_3713() { checkNotSubtype("{int f2}|int","any"); }
	@Test public void test_3714() { checkNotSubtype("{int f2}|int","int|null"); }
	@Test public void test_3715() { checkIsSubtype("{int f2}|int","int"); }
	@Test public void test_3716() { checkNotSubtype("{int f2}|int","any"); }
	@Test public void test_3717() { checkNotSubtype("{int f2}|int","any"); }
	@Test public void test_3718() { checkNotSubtype("{int f2}|int","{null f1}|null"); }
	@Test public void test_3719() { checkNotSubtype("{int f2}|int","{null f2}|null"); }
	@Test public void test_3720() { checkNotSubtype("{int f2}|int","{int f1}|int"); }
	@Test public void test_3721() { checkIsSubtype("{int f2}|int","{int f2}|int"); }

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
