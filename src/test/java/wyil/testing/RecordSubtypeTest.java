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

public class RecordSubtypeTest {
	@Test public void test_78() { checkIsSubtype("null","null"); }
	@Test public void test_79() { checkNotSubtype("null","int"); }
	@Test public void test_82() { checkNotSubtype("null","{null f1}"); }
	@Test public void test_83() { checkNotSubtype("null","{null f2}"); }
	@Test public void test_84() { checkNotSubtype("null","{int f1}"); }
	@Test public void test_85() { checkNotSubtype("null","{int f2}"); }
	@Test public void test_94() { checkNotSubtype("null","{null f1,null f2}"); }
	@Test public void test_95() { checkNotSubtype("null","{null f2,null f3}"); }
	@Test public void test_96() { checkNotSubtype("null","{null f1,int f2}"); }
	@Test public void test_97() { checkNotSubtype("null","{null f2,int f3}"); }
	@Test public void test_100() { checkNotSubtype("null","{int f1,null f2}"); }
	@Test public void test_101() { checkNotSubtype("null","{int f2,null f3}"); }
	@Test public void test_102() { checkNotSubtype("null","{int f1,int f2}"); }
	@Test public void test_103() { checkNotSubtype("null","{int f2,int f3}"); }
	@Test public void test_116() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test public void test_117() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test public void test_118() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test public void test_119() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test public void test_120() { checkNotSubtype("null","{{null f1} f1,null f2}"); }
	@Test public void test_121() { checkNotSubtype("null","{{null f2} f1,null f2}"); }
	@Test public void test_122() { checkNotSubtype("null","{{null f1} f2,null f3}"); }
	@Test public void test_123() { checkNotSubtype("null","{{null f2} f2,null f3}"); }
	@Test public void test_124() { checkNotSubtype("null","{{int f1} f1}"); }
	@Test public void test_125() { checkNotSubtype("null","{{int f2} f1}"); }
	@Test public void test_126() { checkNotSubtype("null","{{int f1} f2}"); }
	@Test public void test_127() { checkNotSubtype("null","{{int f2} f2}"); }
	@Test public void test_128() { checkNotSubtype("null","{{int f1} f1,int f2}"); }
	@Test public void test_129() { checkNotSubtype("null","{{int f2} f1,int f2}"); }
	@Test public void test_130() { checkNotSubtype("null","{{int f1} f2,int f3}"); }
	@Test public void test_131() { checkNotSubtype("null","{{int f2} f2,int f3}"); }
	@Test public void test_133() { checkIsSubtype("null","null"); }
	@Test public void test_134() { checkNotSubtype("null","int"); }
	@Test public void test_139() { checkIsSubtype("null","null"); }
	@Test public void test_141() { checkIsSubtype("null","null"); }
	@Test public void test_142() { checkNotSubtype("null","null|int"); }
	@Test public void test_143() { checkNotSubtype("null","int"); }
	@Test public void test_145() { checkNotSubtype("null","int|null"); }
	@Test public void test_146() { checkNotSubtype("null","int"); }
	@Test public void test_149() { checkNotSubtype("null","{null f1}|null"); }
	@Test public void test_150() { checkNotSubtype("null","{null f2}|null"); }
	@Test public void test_151() { checkNotSubtype("null","{int f1}|int"); }
	@Test public void test_152() { checkNotSubtype("null","{int f2}|int"); }
	@Test public void test_154() { checkNotSubtype("int","null"); }
	@Test public void test_155() { checkIsSubtype("int","int"); }
	@Test public void test_158() { checkNotSubtype("int","{null f1}"); }
	@Test public void test_159() { checkNotSubtype("int","{null f2}"); }
	@Test public void test_160() { checkNotSubtype("int","{int f1}"); }
	@Test public void test_161() { checkNotSubtype("int","{int f2}"); }
	@Test public void test_170() { checkNotSubtype("int","{null f1,null f2}"); }
	@Test public void test_171() { checkNotSubtype("int","{null f2,null f3}"); }
	@Test public void test_172() { checkNotSubtype("int","{null f1,int f2}"); }
	@Test public void test_173() { checkNotSubtype("int","{null f2,int f3}"); }
	@Test public void test_176() { checkNotSubtype("int","{int f1,null f2}"); }
	@Test public void test_177() { checkNotSubtype("int","{int f2,null f3}"); }
	@Test public void test_178() { checkNotSubtype("int","{int f1,int f2}"); }
	@Test public void test_179() { checkNotSubtype("int","{int f2,int f3}"); }
	@Test public void test_192() { checkNotSubtype("int","{{null f1} f1}"); }
	@Test public void test_193() { checkNotSubtype("int","{{null f2} f1}"); }
	@Test public void test_194() { checkNotSubtype("int","{{null f1} f2}"); }
	@Test public void test_195() { checkNotSubtype("int","{{null f2} f2}"); }
	@Test public void test_196() { checkNotSubtype("int","{{null f1} f1,null f2}"); }
	@Test public void test_197() { checkNotSubtype("int","{{null f2} f1,null f2}"); }
	@Test public void test_198() { checkNotSubtype("int","{{null f1} f2,null f3}"); }
	@Test public void test_199() { checkNotSubtype("int","{{null f2} f2,null f3}"); }
	@Test public void test_200() { checkNotSubtype("int","{{int f1} f1}"); }
	@Test public void test_201() { checkNotSubtype("int","{{int f2} f1}"); }
	@Test public void test_202() { checkNotSubtype("int","{{int f1} f2}"); }
	@Test public void test_203() { checkNotSubtype("int","{{int f2} f2}"); }
	@Test public void test_204() { checkNotSubtype("int","{{int f1} f1,int f2}"); }
	@Test public void test_205() { checkNotSubtype("int","{{int f2} f1,int f2}"); }
	@Test public void test_206() { checkNotSubtype("int","{{int f1} f2,int f3}"); }
	@Test public void test_207() { checkNotSubtype("int","{{int f2} f2,int f3}"); }
	@Test public void test_209() { checkNotSubtype("int","null"); }
	@Test public void test_210() { checkIsSubtype("int","int"); }
	@Test public void test_215() { checkNotSubtype("int","null"); }
	@Test public void test_217() { checkNotSubtype("int","null"); }
	@Test public void test_218() { checkNotSubtype("int","null|int"); }
	@Test public void test_219() { checkIsSubtype("int","int"); }
	@Test public void test_221() { checkNotSubtype("int","int|null"); }
	@Test public void test_222() { checkIsSubtype("int","int"); }
	@Test public void test_225() { checkNotSubtype("int","{null f1}|null"); }
	@Test public void test_226() { checkNotSubtype("int","{null f2}|null"); }
	@Test public void test_227() { checkNotSubtype("int","{int f1}|int"); }
	@Test public void test_228() { checkNotSubtype("int","{int f2}|int"); }
	@Test public void test_382() { checkNotSubtype("{null f1}","null"); }
	@Test public void test_383() { checkNotSubtype("{null f1}","int"); }
	@Test public void test_386() { checkIsSubtype("{null f1}","{null f1}"); }
	@Test public void test_387() { checkNotSubtype("{null f1}","{null f2}"); }
	@Test public void test_388() { checkNotSubtype("{null f1}","{int f1}"); }
	@Test public void test_389() { checkNotSubtype("{null f1}","{int f2}"); }
	@Test public void test_398() { checkNotSubtype("{null f1}","{null f1,null f2}"); }
	@Test public void test_399() { checkNotSubtype("{null f1}","{null f2,null f3}"); }
	@Test public void test_400() { checkNotSubtype("{null f1}","{null f1,int f2}"); }
	@Test public void test_401() { checkNotSubtype("{null f1}","{null f2,int f3}"); }
	@Test public void test_404() { checkNotSubtype("{null f1}","{int f1,null f2}"); }
	@Test public void test_405() { checkNotSubtype("{null f1}","{int f2,null f3}"); }
	@Test public void test_406() { checkNotSubtype("{null f1}","{int f1,int f2}"); }
	@Test public void test_407() { checkNotSubtype("{null f1}","{int f2,int f3}"); }
	@Test public void test_420() { checkNotSubtype("{null f1}","{{null f1} f1}"); }
	@Test public void test_421() { checkNotSubtype("{null f1}","{{null f2} f1}"); }
	@Test public void test_422() { checkNotSubtype("{null f1}","{{null f1} f2}"); }
	@Test public void test_423() { checkNotSubtype("{null f1}","{{null f2} f2}"); }
	@Test public void test_424() { checkNotSubtype("{null f1}","{{null f1} f1,null f2}"); }
	@Test public void test_425() { checkNotSubtype("{null f1}","{{null f2} f1,null f2}"); }
	@Test public void test_426() { checkNotSubtype("{null f1}","{{null f1} f2,null f3}"); }
	@Test public void test_427() { checkNotSubtype("{null f1}","{{null f2} f2,null f3}"); }
	@Test public void test_428() { checkNotSubtype("{null f1}","{{int f1} f1}"); }
	@Test public void test_429() { checkNotSubtype("{null f1}","{{int f2} f1}"); }
	@Test public void test_430() { checkNotSubtype("{null f1}","{{int f1} f2}"); }
	@Test public void test_431() { checkNotSubtype("{null f1}","{{int f2} f2}"); }
	@Test public void test_432() { checkNotSubtype("{null f1}","{{int f1} f1,int f2}"); }
	@Test public void test_433() { checkNotSubtype("{null f1}","{{int f2} f1,int f2}"); }
	@Test public void test_434() { checkNotSubtype("{null f1}","{{int f1} f2,int f3}"); }
	@Test public void test_435() { checkNotSubtype("{null f1}","{{int f2} f2,int f3}"); }
	@Test public void test_437() { checkNotSubtype("{null f1}","null"); }
	@Test public void test_438() { checkNotSubtype("{null f1}","int"); }
	@Test public void test_443() { checkNotSubtype("{null f1}","null"); }
	@Test public void test_445() { checkNotSubtype("{null f1}","null"); }
	@Test public void test_446() { checkNotSubtype("{null f1}","null|int"); }
	@Test public void test_447() { checkNotSubtype("{null f1}","int"); }
	@Test public void test_449() { checkNotSubtype("{null f1}","int|null"); }
	@Test public void test_450() { checkNotSubtype("{null f1}","int"); }
	@Test public void test_453() { checkNotSubtype("{null f1}","{null f1}|null"); }
	@Test public void test_454() { checkNotSubtype("{null f1}","{null f2}|null"); }
	@Test public void test_455() { checkNotSubtype("{null f1}","{int f1}|int"); }
	@Test public void test_456() { checkNotSubtype("{null f1}","{int f2}|int"); }
	@Test public void test_458() { checkNotSubtype("{null f2}","null"); }
	@Test public void test_459() { checkNotSubtype("{null f2}","int"); }
	@Test public void test_462() { checkNotSubtype("{null f2}","{null f1}"); }
	@Test public void test_463() { checkIsSubtype("{null f2}","{null f2}"); }
	@Test public void test_464() { checkNotSubtype("{null f2}","{int f1}"); }
	@Test public void test_465() { checkNotSubtype("{null f2}","{int f2}"); }
	@Test public void test_474() { checkNotSubtype("{null f2}","{null f1,null f2}"); }
	@Test public void test_475() { checkNotSubtype("{null f2}","{null f2,null f3}"); }
	@Test public void test_476() { checkNotSubtype("{null f2}","{null f1,int f2}"); }
	@Test public void test_477() { checkNotSubtype("{null f2}","{null f2,int f3}"); }
	@Test public void test_480() { checkNotSubtype("{null f2}","{int f1,null f2}"); }
	@Test public void test_481() { checkNotSubtype("{null f2}","{int f2,null f3}"); }
	@Test public void test_482() { checkNotSubtype("{null f2}","{int f1,int f2}"); }
	@Test public void test_483() { checkNotSubtype("{null f2}","{int f2,int f3}"); }
	@Test public void test_496() { checkNotSubtype("{null f2}","{{null f1} f1}"); }
	@Test public void test_497() { checkNotSubtype("{null f2}","{{null f2} f1}"); }
	@Test public void test_498() { checkNotSubtype("{null f2}","{{null f1} f2}"); }
	@Test public void test_499() { checkNotSubtype("{null f2}","{{null f2} f2}"); }
	@Test public void test_500() { checkNotSubtype("{null f2}","{{null f1} f1,null f2}"); }
	@Test public void test_501() { checkNotSubtype("{null f2}","{{null f2} f1,null f2}"); }
	@Test public void test_502() { checkNotSubtype("{null f2}","{{null f1} f2,null f3}"); }
	@Test public void test_503() { checkNotSubtype("{null f2}","{{null f2} f2,null f3}"); }
	@Test public void test_504() { checkNotSubtype("{null f2}","{{int f1} f1}"); }
	@Test public void test_505() { checkNotSubtype("{null f2}","{{int f2} f1}"); }
	@Test public void test_506() { checkNotSubtype("{null f2}","{{int f1} f2}"); }
	@Test public void test_507() { checkNotSubtype("{null f2}","{{int f2} f2}"); }
	@Test public void test_508() { checkNotSubtype("{null f2}","{{int f1} f1,int f2}"); }
	@Test public void test_509() { checkNotSubtype("{null f2}","{{int f2} f1,int f2}"); }
	@Test public void test_510() { checkNotSubtype("{null f2}","{{int f1} f2,int f3}"); }
	@Test public void test_511() { checkNotSubtype("{null f2}","{{int f2} f2,int f3}"); }
	@Test public void test_513() { checkNotSubtype("{null f2}","null"); }
	@Test public void test_514() { checkNotSubtype("{null f2}","int"); }
	@Test public void test_519() { checkNotSubtype("{null f2}","null"); }
	@Test public void test_521() { checkNotSubtype("{null f2}","null"); }
	@Test public void test_522() { checkNotSubtype("{null f2}","null|int"); }
	@Test public void test_523() { checkNotSubtype("{null f2}","int"); }
	@Test public void test_525() { checkNotSubtype("{null f2}","int|null"); }
	@Test public void test_526() { checkNotSubtype("{null f2}","int"); }
	@Test public void test_529() { checkNotSubtype("{null f2}","{null f1}|null"); }
	@Test public void test_530() { checkNotSubtype("{null f2}","{null f2}|null"); }
	@Test public void test_531() { checkNotSubtype("{null f2}","{int f1}|int"); }
	@Test public void test_532() { checkNotSubtype("{null f2}","{int f2}|int"); }
	@Test public void test_534() { checkNotSubtype("{int f1}","null"); }
	@Test public void test_535() { checkNotSubtype("{int f1}","int"); }
	@Test public void test_538() { checkNotSubtype("{int f1}","{null f1}"); }
	@Test public void test_539() { checkNotSubtype("{int f1}","{null f2}"); }
	@Test public void test_540() { checkIsSubtype("{int f1}","{int f1}"); }
	@Test public void test_541() { checkNotSubtype("{int f1}","{int f2}"); }
	@Test public void test_550() { checkNotSubtype("{int f1}","{null f1,null f2}"); }
	@Test public void test_551() { checkNotSubtype("{int f1}","{null f2,null f3}"); }
	@Test public void test_552() { checkNotSubtype("{int f1}","{null f1,int f2}"); }
	@Test public void test_553() { checkNotSubtype("{int f1}","{null f2,int f3}"); }
	@Test public void test_556() { checkNotSubtype("{int f1}","{int f1,null f2}"); }
	@Test public void test_557() { checkNotSubtype("{int f1}","{int f2,null f3}"); }
	@Test public void test_558() { checkNotSubtype("{int f1}","{int f1,int f2}"); }
	@Test public void test_559() { checkNotSubtype("{int f1}","{int f2,int f3}"); }
	@Test public void test_572() { checkNotSubtype("{int f1}","{{null f1} f1}"); }
	@Test public void test_573() { checkNotSubtype("{int f1}","{{null f2} f1}"); }
	@Test public void test_574() { checkNotSubtype("{int f1}","{{null f1} f2}"); }
	@Test public void test_575() { checkNotSubtype("{int f1}","{{null f2} f2}"); }
	@Test public void test_576() { checkNotSubtype("{int f1}","{{null f1} f1,null f2}"); }
	@Test public void test_577() { checkNotSubtype("{int f1}","{{null f2} f1,null f2}"); }
	@Test public void test_578() { checkNotSubtype("{int f1}","{{null f1} f2,null f3}"); }
	@Test public void test_579() { checkNotSubtype("{int f1}","{{null f2} f2,null f3}"); }
	@Test public void test_580() { checkNotSubtype("{int f1}","{{int f1} f1}"); }
	@Test public void test_581() { checkNotSubtype("{int f1}","{{int f2} f1}"); }
	@Test public void test_582() { checkNotSubtype("{int f1}","{{int f1} f2}"); }
	@Test public void test_583() { checkNotSubtype("{int f1}","{{int f2} f2}"); }
	@Test public void test_584() { checkNotSubtype("{int f1}","{{int f1} f1,int f2}"); }
	@Test public void test_585() { checkNotSubtype("{int f1}","{{int f2} f1,int f2}"); }
	@Test public void test_586() { checkNotSubtype("{int f1}","{{int f1} f2,int f3}"); }
	@Test public void test_587() { checkNotSubtype("{int f1}","{{int f2} f2,int f3}"); }
	@Test public void test_589() { checkNotSubtype("{int f1}","null"); }
	@Test public void test_590() { checkNotSubtype("{int f1}","int"); }
	@Test public void test_595() { checkNotSubtype("{int f1}","null"); }
	@Test public void test_597() { checkNotSubtype("{int f1}","null"); }
	@Test public void test_598() { checkNotSubtype("{int f1}","null|int"); }
	@Test public void test_599() { checkNotSubtype("{int f1}","int"); }
	@Test public void test_601() { checkNotSubtype("{int f1}","int|null"); }
	@Test public void test_602() { checkNotSubtype("{int f1}","int"); }
	@Test public void test_605() { checkNotSubtype("{int f1}","{null f1}|null"); }
	@Test public void test_606() { checkNotSubtype("{int f1}","{null f2}|null"); }
	@Test public void test_607() { checkNotSubtype("{int f1}","{int f1}|int"); }
	@Test public void test_608() { checkNotSubtype("{int f1}","{int f2}|int"); }
	@Test public void test_610() { checkNotSubtype("{int f2}","null"); }
	@Test public void test_611() { checkNotSubtype("{int f2}","int"); }
	@Test public void test_614() { checkNotSubtype("{int f2}","{null f1}"); }
	@Test public void test_615() { checkNotSubtype("{int f2}","{null f2}"); }
	@Test public void test_616() { checkNotSubtype("{int f2}","{int f1}"); }
	@Test public void test_617() { checkIsSubtype("{int f2}","{int f2}"); }
	@Test public void test_626() { checkNotSubtype("{int f2}","{null f1,null f2}"); }
	@Test public void test_627() { checkNotSubtype("{int f2}","{null f2,null f3}"); }
	@Test public void test_628() { checkNotSubtype("{int f2}","{null f1,int f2}"); }
	@Test public void test_629() { checkNotSubtype("{int f2}","{null f2,int f3}"); }
	@Test public void test_632() { checkNotSubtype("{int f2}","{int f1,null f2}"); }
	@Test public void test_633() { checkNotSubtype("{int f2}","{int f2,null f3}"); }
	@Test public void test_634() { checkNotSubtype("{int f2}","{int f1,int f2}"); }
	@Test public void test_635() { checkNotSubtype("{int f2}","{int f2,int f3}"); }
	@Test public void test_648() { checkNotSubtype("{int f2}","{{null f1} f1}"); }
	@Test public void test_649() { checkNotSubtype("{int f2}","{{null f2} f1}"); }
	@Test public void test_650() { checkNotSubtype("{int f2}","{{null f1} f2}"); }
	@Test public void test_651() { checkNotSubtype("{int f2}","{{null f2} f2}"); }
	@Test public void test_652() { checkNotSubtype("{int f2}","{{null f1} f1,null f2}"); }
	@Test public void test_653() { checkNotSubtype("{int f2}","{{null f2} f1,null f2}"); }
	@Test public void test_654() { checkNotSubtype("{int f2}","{{null f1} f2,null f3}"); }
	@Test public void test_655() { checkNotSubtype("{int f2}","{{null f2} f2,null f3}"); }
	@Test public void test_656() { checkNotSubtype("{int f2}","{{int f1} f1}"); }
	@Test public void test_657() { checkNotSubtype("{int f2}","{{int f2} f1}"); }
	@Test public void test_658() { checkNotSubtype("{int f2}","{{int f1} f2}"); }
	@Test public void test_659() { checkNotSubtype("{int f2}","{{int f2} f2}"); }
	@Test public void test_660() { checkNotSubtype("{int f2}","{{int f1} f1,int f2}"); }
	@Test public void test_661() { checkNotSubtype("{int f2}","{{int f2} f1,int f2}"); }
	@Test public void test_662() { checkNotSubtype("{int f2}","{{int f1} f2,int f3}"); }
	@Test public void test_663() { checkNotSubtype("{int f2}","{{int f2} f2,int f3}"); }
	@Test public void test_665() { checkNotSubtype("{int f2}","null"); }
	@Test public void test_666() { checkNotSubtype("{int f2}","int"); }
	@Test public void test_671() { checkNotSubtype("{int f2}","null"); }
	@Test public void test_673() { checkNotSubtype("{int f2}","null"); }
	@Test public void test_674() { checkNotSubtype("{int f2}","null|int"); }
	@Test public void test_675() { checkNotSubtype("{int f2}","int"); }
	@Test public void test_677() { checkNotSubtype("{int f2}","int|null"); }
	@Test public void test_678() { checkNotSubtype("{int f2}","int"); }
	@Test public void test_681() { checkNotSubtype("{int f2}","{null f1}|null"); }
	@Test public void test_682() { checkNotSubtype("{int f2}","{null f2}|null"); }
	@Test public void test_683() { checkNotSubtype("{int f2}","{int f1}|int"); }
	@Test public void test_684() { checkNotSubtype("{int f2}","{int f2}|int"); }
	@Test public void test_1294() { checkNotSubtype("{null f1,null f2}","null"); }
	@Test public void test_1295() { checkNotSubtype("{null f1,null f2}","int"); }
	@Test public void test_1298() { checkNotSubtype("{null f1,null f2}","{null f1}"); }
	@Test public void test_1299() { checkNotSubtype("{null f1,null f2}","{null f2}"); }
	@Test public void test_1300() { checkNotSubtype("{null f1,null f2}","{int f1}"); }
	@Test public void test_1301() { checkNotSubtype("{null f1,null f2}","{int f2}"); }
	@Test public void test_1310() { checkIsSubtype("{null f1,null f2}","{null f1,null f2}"); }
	@Test public void test_1311() { checkNotSubtype("{null f1,null f2}","{null f2,null f3}"); }
	@Test public void test_1312() { checkNotSubtype("{null f1,null f2}","{null f1,int f2}"); }
	@Test public void test_1313() { checkNotSubtype("{null f1,null f2}","{null f2,int f3}"); }
	@Test public void test_1316() { checkNotSubtype("{null f1,null f2}","{int f1,null f2}"); }
	@Test public void test_1317() { checkNotSubtype("{null f1,null f2}","{int f2,null f3}"); }
	@Test public void test_1318() { checkNotSubtype("{null f1,null f2}","{int f1,int f2}"); }
	@Test public void test_1319() { checkNotSubtype("{null f1,null f2}","{int f2,int f3}"); }
	@Test public void test_1332() { checkNotSubtype("{null f1,null f2}","{{null f1} f1}"); }
	@Test public void test_1333() { checkNotSubtype("{null f1,null f2}","{{null f2} f1}"); }
	@Test public void test_1334() { checkNotSubtype("{null f1,null f2}","{{null f1} f2}"); }
	@Test public void test_1335() { checkNotSubtype("{null f1,null f2}","{{null f2} f2}"); }
	@Test public void test_1336() { checkNotSubtype("{null f1,null f2}","{{null f1} f1,null f2}"); }
	@Test public void test_1337() { checkNotSubtype("{null f1,null f2}","{{null f2} f1,null f2}"); }
	@Test public void test_1338() { checkNotSubtype("{null f1,null f2}","{{null f1} f2,null f3}"); }
	@Test public void test_1339() { checkNotSubtype("{null f1,null f2}","{{null f2} f2,null f3}"); }
	@Test public void test_1340() { checkNotSubtype("{null f1,null f2}","{{int f1} f1}"); }
	@Test public void test_1341() { checkNotSubtype("{null f1,null f2}","{{int f2} f1}"); }
	@Test public void test_1342() { checkNotSubtype("{null f1,null f2}","{{int f1} f2}"); }
	@Test public void test_1343() { checkNotSubtype("{null f1,null f2}","{{int f2} f2}"); }
	@Test public void test_1344() { checkNotSubtype("{null f1,null f2}","{{int f1} f1,int f2}"); }
	@Test public void test_1345() { checkNotSubtype("{null f1,null f2}","{{int f2} f1,int f2}"); }
	@Test public void test_1346() { checkNotSubtype("{null f1,null f2}","{{int f1} f2,int f3}"); }
	@Test public void test_1347() { checkNotSubtype("{null f1,null f2}","{{int f2} f2,int f3}"); }
	@Test public void test_1349() { checkNotSubtype("{null f1,null f2}","null"); }
	@Test public void test_1350() { checkNotSubtype("{null f1,null f2}","int"); }
	@Test public void test_1355() { checkNotSubtype("{null f1,null f2}","null"); }
	@Test public void test_1357() { checkNotSubtype("{null f1,null f2}","null"); }
	@Test public void test_1358() { checkNotSubtype("{null f1,null f2}","null|int"); }
	@Test public void test_1359() { checkNotSubtype("{null f1,null f2}","int"); }
	@Test public void test_1361() { checkNotSubtype("{null f1,null f2}","int|null"); }
	@Test public void test_1362() { checkNotSubtype("{null f1,null f2}","int"); }
	@Test public void test_1365() { checkNotSubtype("{null f1,null f2}","{null f1}|null"); }
	@Test public void test_1366() { checkNotSubtype("{null f1,null f2}","{null f2}|null"); }
	@Test public void test_1367() { checkNotSubtype("{null f1,null f2}","{int f1}|int"); }
	@Test public void test_1368() { checkNotSubtype("{null f1,null f2}","{int f2}|int"); }
	@Test public void test_1370() { checkNotSubtype("{null f2,null f3}","null"); }
	@Test public void test_1371() { checkNotSubtype("{null f2,null f3}","int"); }
	@Test public void test_1374() { checkNotSubtype("{null f2,null f3}","{null f1}"); }
	@Test public void test_1375() { checkNotSubtype("{null f2,null f3}","{null f2}"); }
	@Test public void test_1376() { checkNotSubtype("{null f2,null f3}","{int f1}"); }
	@Test public void test_1377() { checkNotSubtype("{null f2,null f3}","{int f2}"); }
	@Test public void test_1386() { checkNotSubtype("{null f2,null f3}","{null f1,null f2}"); }
	@Test public void test_1387() { checkIsSubtype("{null f2,null f3}","{null f2,null f3}"); }
	@Test public void test_1388() { checkNotSubtype("{null f2,null f3}","{null f1,int f2}"); }
	@Test public void test_1389() { checkNotSubtype("{null f2,null f3}","{null f2,int f3}"); }
	@Test public void test_1392() { checkNotSubtype("{null f2,null f3}","{int f1,null f2}"); }
	@Test public void test_1393() { checkNotSubtype("{null f2,null f3}","{int f2,null f3}"); }
	@Test public void test_1394() { checkNotSubtype("{null f2,null f3}","{int f1,int f2}"); }
	@Test public void test_1395() { checkNotSubtype("{null f2,null f3}","{int f2,int f3}"); }
	@Test public void test_1408() { checkNotSubtype("{null f2,null f3}","{{null f1} f1}"); }
	@Test public void test_1409() { checkNotSubtype("{null f2,null f3}","{{null f2} f1}"); }
	@Test public void test_1410() { checkNotSubtype("{null f2,null f3}","{{null f1} f2}"); }
	@Test public void test_1411() { checkNotSubtype("{null f2,null f3}","{{null f2} f2}"); }
	@Test public void test_1412() { checkNotSubtype("{null f2,null f3}","{{null f1} f1,null f2}"); }
	@Test public void test_1413() { checkNotSubtype("{null f2,null f3}","{{null f2} f1,null f2}"); }
	@Test public void test_1414() { checkNotSubtype("{null f2,null f3}","{{null f1} f2,null f3}"); }
	@Test public void test_1415() { checkNotSubtype("{null f2,null f3}","{{null f2} f2,null f3}"); }
	@Test public void test_1416() { checkNotSubtype("{null f2,null f3}","{{int f1} f1}"); }
	@Test public void test_1417() { checkNotSubtype("{null f2,null f3}","{{int f2} f1}"); }
	@Test public void test_1418() { checkNotSubtype("{null f2,null f3}","{{int f1} f2}"); }
	@Test public void test_1419() { checkNotSubtype("{null f2,null f3}","{{int f2} f2}"); }
	@Test public void test_1420() { checkNotSubtype("{null f2,null f3}","{{int f1} f1,int f2}"); }
	@Test public void test_1421() { checkNotSubtype("{null f2,null f3}","{{int f2} f1,int f2}"); }
	@Test public void test_1422() { checkNotSubtype("{null f2,null f3}","{{int f1} f2,int f3}"); }
	@Test public void test_1423() { checkNotSubtype("{null f2,null f3}","{{int f2} f2,int f3}"); }
	@Test public void test_1425() { checkNotSubtype("{null f2,null f3}","null"); }
	@Test public void test_1426() { checkNotSubtype("{null f2,null f3}","int"); }
	@Test public void test_1431() { checkNotSubtype("{null f2,null f3}","null"); }
	@Test public void test_1433() { checkNotSubtype("{null f2,null f3}","null"); }
	@Test public void test_1434() { checkNotSubtype("{null f2,null f3}","null|int"); }
	@Test public void test_1435() { checkNotSubtype("{null f2,null f3}","int"); }
	@Test public void test_1437() { checkNotSubtype("{null f2,null f3}","int|null"); }
	@Test public void test_1438() { checkNotSubtype("{null f2,null f3}","int"); }
	@Test public void test_1441() { checkNotSubtype("{null f2,null f3}","{null f1}|null"); }
	@Test public void test_1442() { checkNotSubtype("{null f2,null f3}","{null f2}|null"); }
	@Test public void test_1443() { checkNotSubtype("{null f2,null f3}","{int f1}|int"); }
	@Test public void test_1444() { checkNotSubtype("{null f2,null f3}","{int f2}|int"); }
	@Test public void test_1446() { checkNotSubtype("{null f1,int f2}","null"); }
	@Test public void test_1447() { checkNotSubtype("{null f1,int f2}","int"); }
	@Test public void test_1450() { checkNotSubtype("{null f1,int f2}","{null f1}"); }
	@Test public void test_1451() { checkNotSubtype("{null f1,int f2}","{null f2}"); }
	@Test public void test_1452() { checkNotSubtype("{null f1,int f2}","{int f1}"); }
	@Test public void test_1453() { checkNotSubtype("{null f1,int f2}","{int f2}"); }
	@Test public void test_1462() { checkNotSubtype("{null f1,int f2}","{null f1,null f2}"); }
	@Test public void test_1463() { checkNotSubtype("{null f1,int f2}","{null f2,null f3}"); }
	@Test public void test_1464() { checkIsSubtype("{null f1,int f2}","{null f1,int f2}"); }
	@Test public void test_1465() { checkNotSubtype("{null f1,int f2}","{null f2,int f3}"); }
	@Test public void test_1468() { checkNotSubtype("{null f1,int f2}","{int f1,null f2}"); }
	@Test public void test_1469() { checkNotSubtype("{null f1,int f2}","{int f2,null f3}"); }
	@Test public void test_1470() { checkNotSubtype("{null f1,int f2}","{int f1,int f2}"); }
	@Test public void test_1471() { checkNotSubtype("{null f1,int f2}","{int f2,int f3}"); }
	@Test public void test_1484() { checkNotSubtype("{null f1,int f2}","{{null f1} f1}"); }
	@Test public void test_1485() { checkNotSubtype("{null f1,int f2}","{{null f2} f1}"); }
	@Test public void test_1486() { checkNotSubtype("{null f1,int f2}","{{null f1} f2}"); }
	@Test public void test_1487() { checkNotSubtype("{null f1,int f2}","{{null f2} f2}"); }
	@Test public void test_1488() { checkNotSubtype("{null f1,int f2}","{{null f1} f1,null f2}"); }
	@Test public void test_1489() { checkNotSubtype("{null f1,int f2}","{{null f2} f1,null f2}"); }
	@Test public void test_1490() { checkNotSubtype("{null f1,int f2}","{{null f1} f2,null f3}"); }
	@Test public void test_1491() { checkNotSubtype("{null f1,int f2}","{{null f2} f2,null f3}"); }
	@Test public void test_1492() { checkNotSubtype("{null f1,int f2}","{{int f1} f1}"); }
	@Test public void test_1493() { checkNotSubtype("{null f1,int f2}","{{int f2} f1}"); }
	@Test public void test_1494() { checkNotSubtype("{null f1,int f2}","{{int f1} f2}"); }
	@Test public void test_1495() { checkNotSubtype("{null f1,int f2}","{{int f2} f2}"); }
	@Test public void test_1496() { checkNotSubtype("{null f1,int f2}","{{int f1} f1,int f2}"); }
	@Test public void test_1497() { checkNotSubtype("{null f1,int f2}","{{int f2} f1,int f2}"); }
	@Test public void test_1498() { checkNotSubtype("{null f1,int f2}","{{int f1} f2,int f3}"); }
	@Test public void test_1499() { checkNotSubtype("{null f1,int f2}","{{int f2} f2,int f3}"); }
	@Test public void test_1501() { checkNotSubtype("{null f1,int f2}","null"); }
	@Test public void test_1502() { checkNotSubtype("{null f1,int f2}","int"); }
	@Test public void test_1507() { checkNotSubtype("{null f1,int f2}","null"); }
	@Test public void test_1509() { checkNotSubtype("{null f1,int f2}","null"); }
	@Test public void test_1510() { checkNotSubtype("{null f1,int f2}","null|int"); }
	@Test public void test_1511() { checkNotSubtype("{null f1,int f2}","int"); }
	@Test public void test_1513() { checkNotSubtype("{null f1,int f2}","int|null"); }
	@Test public void test_1514() { checkNotSubtype("{null f1,int f2}","int"); }
	@Test public void test_1517() { checkNotSubtype("{null f1,int f2}","{null f1}|null"); }
	@Test public void test_1518() { checkNotSubtype("{null f1,int f2}","{null f2}|null"); }
	@Test public void test_1519() { checkNotSubtype("{null f1,int f2}","{int f1}|int"); }
	@Test public void test_1520() { checkNotSubtype("{null f1,int f2}","{int f2}|int"); }
	@Test public void test_1522() { checkNotSubtype("{null f2,int f3}","null"); }
	@Test public void test_1523() { checkNotSubtype("{null f2,int f3}","int"); }
	@Test public void test_1526() { checkNotSubtype("{null f2,int f3}","{null f1}"); }
	@Test public void test_1527() { checkNotSubtype("{null f2,int f3}","{null f2}"); }
	@Test public void test_1528() { checkNotSubtype("{null f2,int f3}","{int f1}"); }
	@Test public void test_1529() { checkNotSubtype("{null f2,int f3}","{int f2}"); }
	@Test public void test_1538() { checkNotSubtype("{null f2,int f3}","{null f1,null f2}"); }
	@Test public void test_1539() { checkNotSubtype("{null f2,int f3}","{null f2,null f3}"); }
	@Test public void test_1540() { checkNotSubtype("{null f2,int f3}","{null f1,int f2}"); }
	@Test public void test_1541() { checkIsSubtype("{null f2,int f3}","{null f2,int f3}"); }
	@Test public void test_1544() { checkNotSubtype("{null f2,int f3}","{int f1,null f2}"); }
	@Test public void test_1545() { checkNotSubtype("{null f2,int f3}","{int f2,null f3}"); }
	@Test public void test_1546() { checkNotSubtype("{null f2,int f3}","{int f1,int f2}"); }
	@Test public void test_1547() { checkNotSubtype("{null f2,int f3}","{int f2,int f3}"); }
	@Test public void test_1560() { checkNotSubtype("{null f2,int f3}","{{null f1} f1}"); }
	@Test public void test_1561() { checkNotSubtype("{null f2,int f3}","{{null f2} f1}"); }
	@Test public void test_1562() { checkNotSubtype("{null f2,int f3}","{{null f1} f2}"); }
	@Test public void test_1563() { checkNotSubtype("{null f2,int f3}","{{null f2} f2}"); }
	@Test public void test_1564() { checkNotSubtype("{null f2,int f3}","{{null f1} f1,null f2}"); }
	@Test public void test_1565() { checkNotSubtype("{null f2,int f3}","{{null f2} f1,null f2}"); }
	@Test public void test_1566() { checkNotSubtype("{null f2,int f3}","{{null f1} f2,null f3}"); }
	@Test public void test_1567() { checkNotSubtype("{null f2,int f3}","{{null f2} f2,null f3}"); }
	@Test public void test_1568() { checkNotSubtype("{null f2,int f3}","{{int f1} f1}"); }
	@Test public void test_1569() { checkNotSubtype("{null f2,int f3}","{{int f2} f1}"); }
	@Test public void test_1570() { checkNotSubtype("{null f2,int f3}","{{int f1} f2}"); }
	@Test public void test_1571() { checkNotSubtype("{null f2,int f3}","{{int f2} f2}"); }
	@Test public void test_1572() { checkNotSubtype("{null f2,int f3}","{{int f1} f1,int f2}"); }
	@Test public void test_1573() { checkNotSubtype("{null f2,int f3}","{{int f2} f1,int f2}"); }
	@Test public void test_1574() { checkNotSubtype("{null f2,int f3}","{{int f1} f2,int f3}"); }
	@Test public void test_1575() { checkNotSubtype("{null f2,int f3}","{{int f2} f2,int f3}"); }
	@Test public void test_1577() { checkNotSubtype("{null f2,int f3}","null"); }
	@Test public void test_1578() { checkNotSubtype("{null f2,int f3}","int"); }
	@Test public void test_1583() { checkNotSubtype("{null f2,int f3}","null"); }
	@Test public void test_1585() { checkNotSubtype("{null f2,int f3}","null"); }
	@Test public void test_1586() { checkNotSubtype("{null f2,int f3}","null|int"); }
	@Test public void test_1587() { checkNotSubtype("{null f2,int f3}","int"); }
	@Test public void test_1589() { checkNotSubtype("{null f2,int f3}","int|null"); }
	@Test public void test_1590() { checkNotSubtype("{null f2,int f3}","int"); }
	@Test public void test_1593() { checkNotSubtype("{null f2,int f3}","{null f1}|null"); }
	@Test public void test_1594() { checkNotSubtype("{null f2,int f3}","{null f2}|null"); }
	@Test public void test_1595() { checkNotSubtype("{null f2,int f3}","{int f1}|int"); }
	@Test public void test_1596() { checkNotSubtype("{null f2,int f3}","{int f2}|int"); }
	@Test public void test_1750() { checkNotSubtype("{int f1,null f2}","null"); }
	@Test public void test_1751() { checkNotSubtype("{int f1,null f2}","int"); }
	@Test public void test_1754() { checkNotSubtype("{int f1,null f2}","{null f1}"); }
	@Test public void test_1755() { checkNotSubtype("{int f1,null f2}","{null f2}"); }
	@Test public void test_1756() { checkNotSubtype("{int f1,null f2}","{int f1}"); }
	@Test public void test_1757() { checkNotSubtype("{int f1,null f2}","{int f2}"); }
	@Test public void test_1766() { checkNotSubtype("{int f1,null f2}","{null f1,null f2}"); }
	@Test public void test_1767() { checkNotSubtype("{int f1,null f2}","{null f2,null f3}"); }
	@Test public void test_1768() { checkNotSubtype("{int f1,null f2}","{null f1,int f2}"); }
	@Test public void test_1769() { checkNotSubtype("{int f1,null f2}","{null f2,int f3}"); }
	@Test public void test_1772() { checkIsSubtype("{int f1,null f2}","{int f1,null f2}"); }
	@Test public void test_1773() { checkNotSubtype("{int f1,null f2}","{int f2,null f3}"); }
	@Test public void test_1774() { checkNotSubtype("{int f1,null f2}","{int f1,int f2}"); }
	@Test public void test_1775() { checkNotSubtype("{int f1,null f2}","{int f2,int f3}"); }
	@Test public void test_1788() { checkNotSubtype("{int f1,null f2}","{{null f1} f1}"); }
	@Test public void test_1789() { checkNotSubtype("{int f1,null f2}","{{null f2} f1}"); }
	@Test public void test_1790() { checkNotSubtype("{int f1,null f2}","{{null f1} f2}"); }
	@Test public void test_1791() { checkNotSubtype("{int f1,null f2}","{{null f2} f2}"); }
	@Test public void test_1792() { checkNotSubtype("{int f1,null f2}","{{null f1} f1,null f2}"); }
	@Test public void test_1793() { checkNotSubtype("{int f1,null f2}","{{null f2} f1,null f2}"); }
	@Test public void test_1794() { checkNotSubtype("{int f1,null f2}","{{null f1} f2,null f3}"); }
	@Test public void test_1795() { checkNotSubtype("{int f1,null f2}","{{null f2} f2,null f3}"); }
	@Test public void test_1796() { checkNotSubtype("{int f1,null f2}","{{int f1} f1}"); }
	@Test public void test_1797() { checkNotSubtype("{int f1,null f2}","{{int f2} f1}"); }
	@Test public void test_1798() { checkNotSubtype("{int f1,null f2}","{{int f1} f2}"); }
	@Test public void test_1799() { checkNotSubtype("{int f1,null f2}","{{int f2} f2}"); }
	@Test public void test_1800() { checkNotSubtype("{int f1,null f2}","{{int f1} f1,int f2}"); }
	@Test public void test_1801() { checkNotSubtype("{int f1,null f2}","{{int f2} f1,int f2}"); }
	@Test public void test_1802() { checkNotSubtype("{int f1,null f2}","{{int f1} f2,int f3}"); }
	@Test public void test_1803() { checkNotSubtype("{int f1,null f2}","{{int f2} f2,int f3}"); }
	@Test public void test_1805() { checkNotSubtype("{int f1,null f2}","null"); }
	@Test public void test_1806() { checkNotSubtype("{int f1,null f2}","int"); }
	@Test public void test_1811() { checkNotSubtype("{int f1,null f2}","null"); }
	@Test public void test_1813() { checkNotSubtype("{int f1,null f2}","null"); }
	@Test public void test_1814() { checkNotSubtype("{int f1,null f2}","null|int"); }
	@Test public void test_1815() { checkNotSubtype("{int f1,null f2}","int"); }
	@Test public void test_1817() { checkNotSubtype("{int f1,null f2}","int|null"); }
	@Test public void test_1818() { checkNotSubtype("{int f1,null f2}","int"); }
	@Test public void test_1821() { checkNotSubtype("{int f1,null f2}","{null f1}|null"); }
	@Test public void test_1822() { checkNotSubtype("{int f1,null f2}","{null f2}|null"); }
	@Test public void test_1823() { checkNotSubtype("{int f1,null f2}","{int f1}|int"); }
	@Test public void test_1824() { checkNotSubtype("{int f1,null f2}","{int f2}|int"); }
	@Test public void test_1826() { checkNotSubtype("{int f2,null f3}","null"); }
	@Test public void test_1827() { checkNotSubtype("{int f2,null f3}","int"); }
	@Test public void test_1830() { checkNotSubtype("{int f2,null f3}","{null f1}"); }
	@Test public void test_1831() { checkNotSubtype("{int f2,null f3}","{null f2}"); }
	@Test public void test_1832() { checkNotSubtype("{int f2,null f3}","{int f1}"); }
	@Test public void test_1833() { checkNotSubtype("{int f2,null f3}","{int f2}"); }
	@Test public void test_1842() { checkNotSubtype("{int f2,null f3}","{null f1,null f2}"); }
	@Test public void test_1843() { checkNotSubtype("{int f2,null f3}","{null f2,null f3}"); }
	@Test public void test_1844() { checkNotSubtype("{int f2,null f3}","{null f1,int f2}"); }
	@Test public void test_1845() { checkNotSubtype("{int f2,null f3}","{null f2,int f3}"); }
	@Test public void test_1848() { checkNotSubtype("{int f2,null f3}","{int f1,null f2}"); }
	@Test public void test_1849() { checkIsSubtype("{int f2,null f3}","{int f2,null f3}"); }
	@Test public void test_1850() { checkNotSubtype("{int f2,null f3}","{int f1,int f2}"); }
	@Test public void test_1851() { checkNotSubtype("{int f2,null f3}","{int f2,int f3}"); }
	@Test public void test_1864() { checkNotSubtype("{int f2,null f3}","{{null f1} f1}"); }
	@Test public void test_1865() { checkNotSubtype("{int f2,null f3}","{{null f2} f1}"); }
	@Test public void test_1866() { checkNotSubtype("{int f2,null f3}","{{null f1} f2}"); }
	@Test public void test_1867() { checkNotSubtype("{int f2,null f3}","{{null f2} f2}"); }
	@Test public void test_1868() { checkNotSubtype("{int f2,null f3}","{{null f1} f1,null f2}"); }
	@Test public void test_1869() { checkNotSubtype("{int f2,null f3}","{{null f2} f1,null f2}"); }
	@Test public void test_1870() { checkNotSubtype("{int f2,null f3}","{{null f1} f2,null f3}"); }
	@Test public void test_1871() { checkNotSubtype("{int f2,null f3}","{{null f2} f2,null f3}"); }
	@Test public void test_1872() { checkNotSubtype("{int f2,null f3}","{{int f1} f1}"); }
	@Test public void test_1873() { checkNotSubtype("{int f2,null f3}","{{int f2} f1}"); }
	@Test public void test_1874() { checkNotSubtype("{int f2,null f3}","{{int f1} f2}"); }
	@Test public void test_1875() { checkNotSubtype("{int f2,null f3}","{{int f2} f2}"); }
	@Test public void test_1876() { checkNotSubtype("{int f2,null f3}","{{int f1} f1,int f2}"); }
	@Test public void test_1877() { checkNotSubtype("{int f2,null f3}","{{int f2} f1,int f2}"); }
	@Test public void test_1878() { checkNotSubtype("{int f2,null f3}","{{int f1} f2,int f3}"); }
	@Test public void test_1879() { checkNotSubtype("{int f2,null f3}","{{int f2} f2,int f3}"); }
	@Test public void test_1881() { checkNotSubtype("{int f2,null f3}","null"); }
	@Test public void test_1882() { checkNotSubtype("{int f2,null f3}","int"); }
	@Test public void test_1887() { checkNotSubtype("{int f2,null f3}","null"); }
	@Test public void test_1889() { checkNotSubtype("{int f2,null f3}","null"); }
	@Test public void test_1890() { checkNotSubtype("{int f2,null f3}","null|int"); }
	@Test public void test_1891() { checkNotSubtype("{int f2,null f3}","int"); }
	@Test public void test_1893() { checkNotSubtype("{int f2,null f3}","int|null"); }
	@Test public void test_1894() { checkNotSubtype("{int f2,null f3}","int"); }
	@Test public void test_1897() { checkNotSubtype("{int f2,null f3}","{null f1}|null"); }
	@Test public void test_1898() { checkNotSubtype("{int f2,null f3}","{null f2}|null"); }
	@Test public void test_1899() { checkNotSubtype("{int f2,null f3}","{int f1}|int"); }
	@Test public void test_1900() { checkNotSubtype("{int f2,null f3}","{int f2}|int"); }
	@Test public void test_1902() { checkNotSubtype("{int f1,int f2}","null"); }
	@Test public void test_1903() { checkNotSubtype("{int f1,int f2}","int"); }
	@Test public void test_1906() { checkNotSubtype("{int f1,int f2}","{null f1}"); }
	@Test public void test_1907() { checkNotSubtype("{int f1,int f2}","{null f2}"); }
	@Test public void test_1908() { checkNotSubtype("{int f1,int f2}","{int f1}"); }
	@Test public void test_1909() { checkNotSubtype("{int f1,int f2}","{int f2}"); }
	@Test public void test_1918() { checkNotSubtype("{int f1,int f2}","{null f1,null f2}"); }
	@Test public void test_1919() { checkNotSubtype("{int f1,int f2}","{null f2,null f3}"); }
	@Test public void test_1920() { checkNotSubtype("{int f1,int f2}","{null f1,int f2}"); }
	@Test public void test_1921() { checkNotSubtype("{int f1,int f2}","{null f2,int f3}"); }
	@Test public void test_1924() { checkNotSubtype("{int f1,int f2}","{int f1,null f2}"); }
	@Test public void test_1925() { checkNotSubtype("{int f1,int f2}","{int f2,null f3}"); }
	@Test public void test_1926() { checkIsSubtype("{int f1,int f2}","{int f1,int f2}"); }
	@Test public void test_1927() { checkNotSubtype("{int f1,int f2}","{int f2,int f3}"); }
	@Test public void test_1940() { checkNotSubtype("{int f1,int f2}","{{null f1} f1}"); }
	@Test public void test_1941() { checkNotSubtype("{int f1,int f2}","{{null f2} f1}"); }
	@Test public void test_1942() { checkNotSubtype("{int f1,int f2}","{{null f1} f2}"); }
	@Test public void test_1943() { checkNotSubtype("{int f1,int f2}","{{null f2} f2}"); }
	@Test public void test_1944() { checkNotSubtype("{int f1,int f2}","{{null f1} f1,null f2}"); }
	@Test public void test_1945() { checkNotSubtype("{int f1,int f2}","{{null f2} f1,null f2}"); }
	@Test public void test_1946() { checkNotSubtype("{int f1,int f2}","{{null f1} f2,null f3}"); }
	@Test public void test_1947() { checkNotSubtype("{int f1,int f2}","{{null f2} f2,null f3}"); }
	@Test public void test_1948() { checkNotSubtype("{int f1,int f2}","{{int f1} f1}"); }
	@Test public void test_1949() { checkNotSubtype("{int f1,int f2}","{{int f2} f1}"); }
	@Test public void test_1950() { checkNotSubtype("{int f1,int f2}","{{int f1} f2}"); }
	@Test public void test_1951() { checkNotSubtype("{int f1,int f2}","{{int f2} f2}"); }
	@Test public void test_1952() { checkNotSubtype("{int f1,int f2}","{{int f1} f1,int f2}"); }
	@Test public void test_1953() { checkNotSubtype("{int f1,int f2}","{{int f2} f1,int f2}"); }
	@Test public void test_1954() { checkNotSubtype("{int f1,int f2}","{{int f1} f2,int f3}"); }
	@Test public void test_1955() { checkNotSubtype("{int f1,int f2}","{{int f2} f2,int f3}"); }
	@Test public void test_1957() { checkNotSubtype("{int f1,int f2}","null"); }
	@Test public void test_1958() { checkNotSubtype("{int f1,int f2}","int"); }
	@Test public void test_1963() { checkNotSubtype("{int f1,int f2}","null"); }
	@Test public void test_1965() { checkNotSubtype("{int f1,int f2}","null"); }
	@Test public void test_1966() { checkNotSubtype("{int f1,int f2}","null|int"); }
	@Test public void test_1967() { checkNotSubtype("{int f1,int f2}","int"); }
	@Test public void test_1969() { checkNotSubtype("{int f1,int f2}","int|null"); }
	@Test public void test_1970() { checkNotSubtype("{int f1,int f2}","int"); }
	@Test public void test_1973() { checkNotSubtype("{int f1,int f2}","{null f1}|null"); }
	@Test public void test_1974() { checkNotSubtype("{int f1,int f2}","{null f2}|null"); }
	@Test public void test_1975() { checkNotSubtype("{int f1,int f2}","{int f1}|int"); }
	@Test public void test_1976() { checkNotSubtype("{int f1,int f2}","{int f2}|int"); }
	@Test public void test_1978() { checkNotSubtype("{int f2,int f3}","null"); }
	@Test public void test_1979() { checkNotSubtype("{int f2,int f3}","int"); }
	@Test public void test_1982() { checkNotSubtype("{int f2,int f3}","{null f1}"); }
	@Test public void test_1983() { checkNotSubtype("{int f2,int f3}","{null f2}"); }
	@Test public void test_1984() { checkNotSubtype("{int f2,int f3}","{int f1}"); }
	@Test public void test_1985() { checkNotSubtype("{int f2,int f3}","{int f2}"); }
	@Test public void test_1994() { checkNotSubtype("{int f2,int f3}","{null f1,null f2}"); }
	@Test public void test_1995() { checkNotSubtype("{int f2,int f3}","{null f2,null f3}"); }
	@Test public void test_1996() { checkNotSubtype("{int f2,int f3}","{null f1,int f2}"); }
	@Test public void test_1997() { checkNotSubtype("{int f2,int f3}","{null f2,int f3}"); }
	@Test public void test_2000() { checkNotSubtype("{int f2,int f3}","{int f1,null f2}"); }
	@Test public void test_2001() { checkNotSubtype("{int f2,int f3}","{int f2,null f3}"); }
	@Test public void test_2002() { checkNotSubtype("{int f2,int f3}","{int f1,int f2}"); }
	@Test public void test_2003() { checkIsSubtype("{int f2,int f3}","{int f2,int f3}"); }
	@Test public void test_2016() { checkNotSubtype("{int f2,int f3}","{{null f1} f1}"); }
	@Test public void test_2017() { checkNotSubtype("{int f2,int f3}","{{null f2} f1}"); }
	@Test public void test_2018() { checkNotSubtype("{int f2,int f3}","{{null f1} f2}"); }
	@Test public void test_2019() { checkNotSubtype("{int f2,int f3}","{{null f2} f2}"); }
	@Test public void test_2020() { checkNotSubtype("{int f2,int f3}","{{null f1} f1,null f2}"); }
	@Test public void test_2021() { checkNotSubtype("{int f2,int f3}","{{null f2} f1,null f2}"); }
	@Test public void test_2022() { checkNotSubtype("{int f2,int f3}","{{null f1} f2,null f3}"); }
	@Test public void test_2023() { checkNotSubtype("{int f2,int f3}","{{null f2} f2,null f3}"); }
	@Test public void test_2024() { checkNotSubtype("{int f2,int f3}","{{int f1} f1}"); }
	@Test public void test_2025() { checkNotSubtype("{int f2,int f3}","{{int f2} f1}"); }
	@Test public void test_2026() { checkNotSubtype("{int f2,int f3}","{{int f1} f2}"); }
	@Test public void test_2027() { checkNotSubtype("{int f2,int f3}","{{int f2} f2}"); }
	@Test public void test_2028() { checkNotSubtype("{int f2,int f3}","{{int f1} f1,int f2}"); }
	@Test public void test_2029() { checkNotSubtype("{int f2,int f3}","{{int f2} f1,int f2}"); }
	@Test public void test_2030() { checkNotSubtype("{int f2,int f3}","{{int f1} f2,int f3}"); }
	@Test public void test_2031() { checkNotSubtype("{int f2,int f3}","{{int f2} f2,int f3}"); }
	@Test public void test_2033() { checkNotSubtype("{int f2,int f3}","null"); }
	@Test public void test_2034() { checkNotSubtype("{int f2,int f3}","int"); }
	@Test public void test_2039() { checkNotSubtype("{int f2,int f3}","null"); }
	@Test public void test_2041() { checkNotSubtype("{int f2,int f3}","null"); }
	@Test public void test_2042() { checkNotSubtype("{int f2,int f3}","null|int"); }
	@Test public void test_2043() { checkNotSubtype("{int f2,int f3}","int"); }
	@Test public void test_2045() { checkNotSubtype("{int f2,int f3}","int|null"); }
	@Test public void test_2046() { checkNotSubtype("{int f2,int f3}","int"); }
	@Test public void test_2049() { checkNotSubtype("{int f2,int f3}","{null f1}|null"); }
	@Test public void test_2050() { checkNotSubtype("{int f2,int f3}","{null f2}|null"); }
	@Test public void test_2051() { checkNotSubtype("{int f2,int f3}","{int f1}|int"); }
	@Test public void test_2052() { checkNotSubtype("{int f2,int f3}","{int f2}|int"); }
	@Test public void test_2966() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test public void test_2967() { checkNotSubtype("{{null f1} f1}","int"); }
	@Test public void test_2970() { checkNotSubtype("{{null f1} f1}","{null f1}"); }
	@Test public void test_2971() { checkNotSubtype("{{null f1} f1}","{null f2}"); }
	@Test public void test_2972() { checkNotSubtype("{{null f1} f1}","{int f1}"); }
	@Test public void test_2973() { checkNotSubtype("{{null f1} f1}","{int f2}"); }
	@Test public void test_2982() { checkNotSubtype("{{null f1} f1}","{null f1,null f2}"); }
	@Test public void test_2983() { checkNotSubtype("{{null f1} f1}","{null f2,null f3}"); }
	@Test public void test_2984() { checkNotSubtype("{{null f1} f1}","{null f1,int f2}"); }
	@Test public void test_2985() { checkNotSubtype("{{null f1} f1}","{null f2,int f3}"); }
	@Test public void test_2988() { checkNotSubtype("{{null f1} f1}","{int f1,null f2}"); }
	@Test public void test_2989() { checkNotSubtype("{{null f1} f1}","{int f2,null f3}"); }
	@Test public void test_2990() { checkNotSubtype("{{null f1} f1}","{int f1,int f2}"); }
	@Test public void test_2991() { checkNotSubtype("{{null f1} f1}","{int f2,int f3}"); }
	@Test public void test_3004() { checkIsSubtype("{{null f1} f1}","{{null f1} f1}"); }
	@Test public void test_3005() { checkNotSubtype("{{null f1} f1}","{{null f2} f1}"); }
	@Test public void test_3006() { checkNotSubtype("{{null f1} f1}","{{null f1} f2}"); }
	@Test public void test_3007() { checkNotSubtype("{{null f1} f1}","{{null f2} f2}"); }
	@Test public void test_3008() { checkNotSubtype("{{null f1} f1}","{{null f1} f1,null f2}"); }
	@Test public void test_3009() { checkNotSubtype("{{null f1} f1}","{{null f2} f1,null f2}"); }
	@Test public void test_3010() { checkNotSubtype("{{null f1} f1}","{{null f1} f2,null f3}"); }
	@Test public void test_3011() { checkNotSubtype("{{null f1} f1}","{{null f2} f2,null f3}"); }
	@Test public void test_3012() { checkNotSubtype("{{null f1} f1}","{{int f1} f1}"); }
	@Test public void test_3013() { checkNotSubtype("{{null f1} f1}","{{int f2} f1}"); }
	@Test public void test_3014() { checkNotSubtype("{{null f1} f1}","{{int f1} f2}"); }
	@Test public void test_3015() { checkNotSubtype("{{null f1} f1}","{{int f2} f2}"); }
	@Test public void test_3016() { checkNotSubtype("{{null f1} f1}","{{int f1} f1,int f2}"); }
	@Test public void test_3017() { checkNotSubtype("{{null f1} f1}","{{int f2} f1,int f2}"); }
	@Test public void test_3018() { checkNotSubtype("{{null f1} f1}","{{int f1} f2,int f3}"); }
	@Test public void test_3019() { checkNotSubtype("{{null f1} f1}","{{int f2} f2,int f3}"); }
	@Test public void test_3021() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test public void test_3022() { checkNotSubtype("{{null f1} f1}","int"); }
	@Test public void test_3027() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test public void test_3029() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test public void test_3030() { checkNotSubtype("{{null f1} f1}","null|int"); }
	@Test public void test_3031() { checkNotSubtype("{{null f1} f1}","int"); }
	@Test public void test_3033() { checkNotSubtype("{{null f1} f1}","int|null"); }
	@Test public void test_3034() { checkNotSubtype("{{null f1} f1}","int"); }
	@Test public void test_3037() { checkNotSubtype("{{null f1} f1}","{null f1}|null"); }
	@Test public void test_3038() { checkNotSubtype("{{null f1} f1}","{null f2}|null"); }
	@Test public void test_3039() { checkNotSubtype("{{null f1} f1}","{int f1}|int"); }
	@Test public void test_3040() { checkNotSubtype("{{null f1} f1}","{int f2}|int"); }
	@Test public void test_3042() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test public void test_3043() { checkNotSubtype("{{null f2} f1}","int"); }
	@Test public void test_3046() { checkNotSubtype("{{null f2} f1}","{null f1}"); }
	@Test public void test_3047() { checkNotSubtype("{{null f2} f1}","{null f2}"); }
	@Test public void test_3048() { checkNotSubtype("{{null f2} f1}","{int f1}"); }
	@Test public void test_3049() { checkNotSubtype("{{null f2} f1}","{int f2}"); }
	@Test public void test_3058() { checkNotSubtype("{{null f2} f1}","{null f1,null f2}"); }
	@Test public void test_3059() { checkNotSubtype("{{null f2} f1}","{null f2,null f3}"); }
	@Test public void test_3060() { checkNotSubtype("{{null f2} f1}","{null f1,int f2}"); }
	@Test public void test_3061() { checkNotSubtype("{{null f2} f1}","{null f2,int f3}"); }
	@Test public void test_3064() { checkNotSubtype("{{null f2} f1}","{int f1,null f2}"); }
	@Test public void test_3065() { checkNotSubtype("{{null f2} f1}","{int f2,null f3}"); }
	@Test public void test_3066() { checkNotSubtype("{{null f2} f1}","{int f1,int f2}"); }
	@Test public void test_3067() { checkNotSubtype("{{null f2} f1}","{int f2,int f3}"); }
	@Test public void test_3080() { checkNotSubtype("{{null f2} f1}","{{null f1} f1}"); }
	@Test public void test_3081() { checkIsSubtype("{{null f2} f1}","{{null f2} f1}"); }
	@Test public void test_3082() { checkNotSubtype("{{null f2} f1}","{{null f1} f2}"); }
	@Test public void test_3083() { checkNotSubtype("{{null f2} f1}","{{null f2} f2}"); }
	@Test public void test_3084() { checkNotSubtype("{{null f2} f1}","{{null f1} f1,null f2}"); }
	@Test public void test_3085() { checkNotSubtype("{{null f2} f1}","{{null f2} f1,null f2}"); }
	@Test public void test_3086() { checkNotSubtype("{{null f2} f1}","{{null f1} f2,null f3}"); }
	@Test public void test_3087() { checkNotSubtype("{{null f2} f1}","{{null f2} f2,null f3}"); }
	@Test public void test_3088() { checkNotSubtype("{{null f2} f1}","{{int f1} f1}"); }
	@Test public void test_3089() { checkNotSubtype("{{null f2} f1}","{{int f2} f1}"); }
	@Test public void test_3090() { checkNotSubtype("{{null f2} f1}","{{int f1} f2}"); }
	@Test public void test_3091() { checkNotSubtype("{{null f2} f1}","{{int f2} f2}"); }
	@Test public void test_3092() { checkNotSubtype("{{null f2} f1}","{{int f1} f1,int f2}"); }
	@Test public void test_3093() { checkNotSubtype("{{null f2} f1}","{{int f2} f1,int f2}"); }
	@Test public void test_3094() { checkNotSubtype("{{null f2} f1}","{{int f1} f2,int f3}"); }
	@Test public void test_3095() { checkNotSubtype("{{null f2} f1}","{{int f2} f2,int f3}"); }
	@Test public void test_3097() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test public void test_3098() { checkNotSubtype("{{null f2} f1}","int"); }
	@Test public void test_3103() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test public void test_3105() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test public void test_3106() { checkNotSubtype("{{null f2} f1}","null|int"); }
	@Test public void test_3107() { checkNotSubtype("{{null f2} f1}","int"); }
	@Test public void test_3109() { checkNotSubtype("{{null f2} f1}","int|null"); }
	@Test public void test_3110() { checkNotSubtype("{{null f2} f1}","int"); }
	@Test public void test_3113() { checkNotSubtype("{{null f2} f1}","{null f1}|null"); }
	@Test public void test_3114() { checkNotSubtype("{{null f2} f1}","{null f2}|null"); }
	@Test public void test_3115() { checkNotSubtype("{{null f2} f1}","{int f1}|int"); }
	@Test public void test_3116() { checkNotSubtype("{{null f2} f1}","{int f2}|int"); }
	@Test public void test_3118() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test public void test_3119() { checkNotSubtype("{{null f1} f2}","int"); }
	@Test public void test_3122() { checkNotSubtype("{{null f1} f2}","{null f1}"); }
	@Test public void test_3123() { checkNotSubtype("{{null f1} f2}","{null f2}"); }
	@Test public void test_3124() { checkNotSubtype("{{null f1} f2}","{int f1}"); }
	@Test public void test_3125() { checkNotSubtype("{{null f1} f2}","{int f2}"); }
	@Test public void test_3134() { checkNotSubtype("{{null f1} f2}","{null f1,null f2}"); }
	@Test public void test_3135() { checkNotSubtype("{{null f1} f2}","{null f2,null f3}"); }
	@Test public void test_3136() { checkNotSubtype("{{null f1} f2}","{null f1,int f2}"); }
	@Test public void test_3137() { checkNotSubtype("{{null f1} f2}","{null f2,int f3}"); }
	@Test public void test_3140() { checkNotSubtype("{{null f1} f2}","{int f1,null f2}"); }
	@Test public void test_3141() { checkNotSubtype("{{null f1} f2}","{int f2,null f3}"); }
	@Test public void test_3142() { checkNotSubtype("{{null f1} f2}","{int f1,int f2}"); }
	@Test public void test_3143() { checkNotSubtype("{{null f1} f2}","{int f2,int f3}"); }
	@Test public void test_3156() { checkNotSubtype("{{null f1} f2}","{{null f1} f1}"); }
	@Test public void test_3157() { checkNotSubtype("{{null f1} f2}","{{null f2} f1}"); }
	@Test public void test_3158() { checkIsSubtype("{{null f1} f2}","{{null f1} f2}"); }
	@Test public void test_3159() { checkNotSubtype("{{null f1} f2}","{{null f2} f2}"); }
	@Test public void test_3160() { checkNotSubtype("{{null f1} f2}","{{null f1} f1,null f2}"); }
	@Test public void test_3161() { checkNotSubtype("{{null f1} f2}","{{null f2} f1,null f2}"); }
	@Test public void test_3162() { checkNotSubtype("{{null f1} f2}","{{null f1} f2,null f3}"); }
	@Test public void test_3163() { checkNotSubtype("{{null f1} f2}","{{null f2} f2,null f3}"); }
	@Test public void test_3164() { checkNotSubtype("{{null f1} f2}","{{int f1} f1}"); }
	@Test public void test_3165() { checkNotSubtype("{{null f1} f2}","{{int f2} f1}"); }
	@Test public void test_3166() { checkNotSubtype("{{null f1} f2}","{{int f1} f2}"); }
	@Test public void test_3167() { checkNotSubtype("{{null f1} f2}","{{int f2} f2}"); }
	@Test public void test_3168() { checkNotSubtype("{{null f1} f2}","{{int f1} f1,int f2}"); }
	@Test public void test_3169() { checkNotSubtype("{{null f1} f2}","{{int f2} f1,int f2}"); }
	@Test public void test_3170() { checkNotSubtype("{{null f1} f2}","{{int f1} f2,int f3}"); }
	@Test public void test_3171() { checkNotSubtype("{{null f1} f2}","{{int f2} f2,int f3}"); }
	@Test public void test_3173() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test public void test_3174() { checkNotSubtype("{{null f1} f2}","int"); }
	@Test public void test_3179() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test public void test_3181() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test public void test_3182() { checkNotSubtype("{{null f1} f2}","null|int"); }
	@Test public void test_3183() { checkNotSubtype("{{null f1} f2}","int"); }
	@Test public void test_3185() { checkNotSubtype("{{null f1} f2}","int|null"); }
	@Test public void test_3186() { checkNotSubtype("{{null f1} f2}","int"); }
	@Test public void test_3189() { checkNotSubtype("{{null f1} f2}","{null f1}|null"); }
	@Test public void test_3190() { checkNotSubtype("{{null f1} f2}","{null f2}|null"); }
	@Test public void test_3191() { checkNotSubtype("{{null f1} f2}","{int f1}|int"); }
	@Test public void test_3192() { checkNotSubtype("{{null f1} f2}","{int f2}|int"); }
	@Test public void test_3194() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test public void test_3195() { checkNotSubtype("{{null f2} f2}","int"); }
	@Test public void test_3198() { checkNotSubtype("{{null f2} f2}","{null f1}"); }
	@Test public void test_3199() { checkNotSubtype("{{null f2} f2}","{null f2}"); }
	@Test public void test_3200() { checkNotSubtype("{{null f2} f2}","{int f1}"); }
	@Test public void test_3201() { checkNotSubtype("{{null f2} f2}","{int f2}"); }
	@Test public void test_3210() { checkNotSubtype("{{null f2} f2}","{null f1,null f2}"); }
	@Test public void test_3211() { checkNotSubtype("{{null f2} f2}","{null f2,null f3}"); }
	@Test public void test_3212() { checkNotSubtype("{{null f2} f2}","{null f1,int f2}"); }
	@Test public void test_3213() { checkNotSubtype("{{null f2} f2}","{null f2,int f3}"); }
	@Test public void test_3216() { checkNotSubtype("{{null f2} f2}","{int f1,null f2}"); }
	@Test public void test_3217() { checkNotSubtype("{{null f2} f2}","{int f2,null f3}"); }
	@Test public void test_3218() { checkNotSubtype("{{null f2} f2}","{int f1,int f2}"); }
	@Test public void test_3219() { checkNotSubtype("{{null f2} f2}","{int f2,int f3}"); }
	@Test public void test_3232() { checkNotSubtype("{{null f2} f2}","{{null f1} f1}"); }
	@Test public void test_3233() { checkNotSubtype("{{null f2} f2}","{{null f2} f1}"); }
	@Test public void test_3234() { checkNotSubtype("{{null f2} f2}","{{null f1} f2}"); }
	@Test public void test_3235() { checkIsSubtype("{{null f2} f2}","{{null f2} f2}"); }
	@Test public void test_3236() { checkNotSubtype("{{null f2} f2}","{{null f1} f1,null f2}"); }
	@Test public void test_3237() { checkNotSubtype("{{null f2} f2}","{{null f2} f1,null f2}"); }
	@Test public void test_3238() { checkNotSubtype("{{null f2} f2}","{{null f1} f2,null f3}"); }
	@Test public void test_3239() { checkNotSubtype("{{null f2} f2}","{{null f2} f2,null f3}"); }
	@Test public void test_3240() { checkNotSubtype("{{null f2} f2}","{{int f1} f1}"); }
	@Test public void test_3241() { checkNotSubtype("{{null f2} f2}","{{int f2} f1}"); }
	@Test public void test_3242() { checkNotSubtype("{{null f2} f2}","{{int f1} f2}"); }
	@Test public void test_3243() { checkNotSubtype("{{null f2} f2}","{{int f2} f2}"); }
	@Test public void test_3244() { checkNotSubtype("{{null f2} f2}","{{int f1} f1,int f2}"); }
	@Test public void test_3245() { checkNotSubtype("{{null f2} f2}","{{int f2} f1,int f2}"); }
	@Test public void test_3246() { checkNotSubtype("{{null f2} f2}","{{int f1} f2,int f3}"); }
	@Test public void test_3247() { checkNotSubtype("{{null f2} f2}","{{int f2} f2,int f3}"); }
	@Test public void test_3249() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test public void test_3250() { checkNotSubtype("{{null f2} f2}","int"); }
	@Test public void test_3255() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test public void test_3257() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test public void test_3258() { checkNotSubtype("{{null f2} f2}","null|int"); }
	@Test public void test_3259() { checkNotSubtype("{{null f2} f2}","int"); }
	@Test public void test_3261() { checkNotSubtype("{{null f2} f2}","int|null"); }
	@Test public void test_3262() { checkNotSubtype("{{null f2} f2}","int"); }
	@Test public void test_3265() { checkNotSubtype("{{null f2} f2}","{null f1}|null"); }
	@Test public void test_3266() { checkNotSubtype("{{null f2} f2}","{null f2}|null"); }
	@Test public void test_3267() { checkNotSubtype("{{null f2} f2}","{int f1}|int"); }
	@Test public void test_3268() { checkNotSubtype("{{null f2} f2}","{int f2}|int"); }
	@Test public void test_3270() { checkNotSubtype("{{null f1} f1,null f2}","null"); }
	@Test public void test_3271() { checkNotSubtype("{{null f1} f1,null f2}","int"); }
	@Test public void test_3274() { checkNotSubtype("{{null f1} f1,null f2}","{null f1}"); }
	@Test public void test_3275() { checkNotSubtype("{{null f1} f1,null f2}","{null f2}"); }
	@Test public void test_3276() { checkNotSubtype("{{null f1} f1,null f2}","{int f1}"); }
	@Test public void test_3277() { checkNotSubtype("{{null f1} f1,null f2}","{int f2}"); }
	@Test public void test_3286() { checkNotSubtype("{{null f1} f1,null f2}","{null f1,null f2}"); }
	@Test public void test_3287() { checkNotSubtype("{{null f1} f1,null f2}","{null f2,null f3}"); }
	@Test public void test_3288() { checkNotSubtype("{{null f1} f1,null f2}","{null f1,int f2}"); }
	@Test public void test_3289() { checkNotSubtype("{{null f1} f1,null f2}","{null f2,int f3}"); }
	@Test public void test_3292() { checkNotSubtype("{{null f1} f1,null f2}","{int f1,null f2}"); }
	@Test public void test_3293() { checkNotSubtype("{{null f1} f1,null f2}","{int f2,null f3}"); }
	@Test public void test_3294() { checkNotSubtype("{{null f1} f1,null f2}","{int f1,int f2}"); }
	@Test public void test_3295() { checkNotSubtype("{{null f1} f1,null f2}","{int f2,int f3}"); }
	@Test public void test_3308() { checkNotSubtype("{{null f1} f1,null f2}","{{null f1} f1}"); }
	@Test public void test_3309() { checkNotSubtype("{{null f1} f1,null f2}","{{null f2} f1}"); }
	@Test public void test_3310() { checkNotSubtype("{{null f1} f1,null f2}","{{null f1} f2}"); }
	@Test public void test_3311() { checkNotSubtype("{{null f1} f1,null f2}","{{null f2} f2}"); }
	@Test public void test_3312() { checkIsSubtype("{{null f1} f1,null f2}","{{null f1} f1,null f2}"); }
	@Test public void test_3313() { checkNotSubtype("{{null f1} f1,null f2}","{{null f2} f1,null f2}"); }
	@Test public void test_3314() { checkNotSubtype("{{null f1} f1,null f2}","{{null f1} f2,null f3}"); }
	@Test public void test_3315() { checkNotSubtype("{{null f1} f1,null f2}","{{null f2} f2,null f3}"); }
	@Test public void test_3316() { checkNotSubtype("{{null f1} f1,null f2}","{{int f1} f1}"); }
	@Test public void test_3317() { checkNotSubtype("{{null f1} f1,null f2}","{{int f2} f1}"); }
	@Test public void test_3318() { checkNotSubtype("{{null f1} f1,null f2}","{{int f1} f2}"); }
	@Test public void test_3319() { checkNotSubtype("{{null f1} f1,null f2}","{{int f2} f2}"); }
	@Test public void test_3320() { checkNotSubtype("{{null f1} f1,null f2}","{{int f1} f1,int f2}"); }
	@Test public void test_3321() { checkNotSubtype("{{null f1} f1,null f2}","{{int f2} f1,int f2}"); }
	@Test public void test_3322() { checkNotSubtype("{{null f1} f1,null f2}","{{int f1} f2,int f3}"); }
	@Test public void test_3323() { checkNotSubtype("{{null f1} f1,null f2}","{{int f2} f2,int f3}"); }
	@Test public void test_3325() { checkNotSubtype("{{null f1} f1,null f2}","null"); }
	@Test public void test_3326() { checkNotSubtype("{{null f1} f1,null f2}","int"); }
	@Test public void test_3331() { checkNotSubtype("{{null f1} f1,null f2}","null"); }
	@Test public void test_3333() { checkNotSubtype("{{null f1} f1,null f2}","null"); }
	@Test public void test_3334() { checkNotSubtype("{{null f1} f1,null f2}","null|int"); }
	@Test public void test_3335() { checkNotSubtype("{{null f1} f1,null f2}","int"); }
	@Test public void test_3337() { checkNotSubtype("{{null f1} f1,null f2}","int|null"); }
	@Test public void test_3338() { checkNotSubtype("{{null f1} f1,null f2}","int"); }
	@Test public void test_3341() { checkNotSubtype("{{null f1} f1,null f2}","{null f1}|null"); }
	@Test public void test_3342() { checkNotSubtype("{{null f1} f1,null f2}","{null f2}|null"); }
	@Test public void test_3343() { checkNotSubtype("{{null f1} f1,null f2}","{int f1}|int"); }
	@Test public void test_3344() { checkNotSubtype("{{null f1} f1,null f2}","{int f2}|int"); }
	@Test public void test_3346() { checkNotSubtype("{{null f2} f1,null f2}","null"); }
	@Test public void test_3347() { checkNotSubtype("{{null f2} f1,null f2}","int"); }
	@Test public void test_3350() { checkNotSubtype("{{null f2} f1,null f2}","{null f1}"); }
	@Test public void test_3351() { checkNotSubtype("{{null f2} f1,null f2}","{null f2}"); }
	@Test public void test_3352() { checkNotSubtype("{{null f2} f1,null f2}","{int f1}"); }
	@Test public void test_3353() { checkNotSubtype("{{null f2} f1,null f2}","{int f2}"); }
	@Test public void test_3362() { checkNotSubtype("{{null f2} f1,null f2}","{null f1,null f2}"); }
	@Test public void test_3363() { checkNotSubtype("{{null f2} f1,null f2}","{null f2,null f3}"); }
	@Test public void test_3364() { checkNotSubtype("{{null f2} f1,null f2}","{null f1,int f2}"); }
	@Test public void test_3365() { checkNotSubtype("{{null f2} f1,null f2}","{null f2,int f3}"); }
	@Test public void test_3368() { checkNotSubtype("{{null f2} f1,null f2}","{int f1,null f2}"); }
	@Test public void test_3369() { checkNotSubtype("{{null f2} f1,null f2}","{int f2,null f3}"); }
	@Test public void test_3370() { checkNotSubtype("{{null f2} f1,null f2}","{int f1,int f2}"); }
	@Test public void test_3371() { checkNotSubtype("{{null f2} f1,null f2}","{int f2,int f3}"); }
	@Test public void test_3384() { checkNotSubtype("{{null f2} f1,null f2}","{{null f1} f1}"); }
	@Test public void test_3385() { checkNotSubtype("{{null f2} f1,null f2}","{{null f2} f1}"); }
	@Test public void test_3386() { checkNotSubtype("{{null f2} f1,null f2}","{{null f1} f2}"); }
	@Test public void test_3387() { checkNotSubtype("{{null f2} f1,null f2}","{{null f2} f2}"); }
	@Test public void test_3388() { checkNotSubtype("{{null f2} f1,null f2}","{{null f1} f1,null f2}"); }
	@Test public void test_3389() { checkIsSubtype("{{null f2} f1,null f2}","{{null f2} f1,null f2}"); }
	@Test public void test_3390() { checkNotSubtype("{{null f2} f1,null f2}","{{null f1} f2,null f3}"); }
	@Test public void test_3391() { checkNotSubtype("{{null f2} f1,null f2}","{{null f2} f2,null f3}"); }
	@Test public void test_3392() { checkNotSubtype("{{null f2} f1,null f2}","{{int f1} f1}"); }
	@Test public void test_3393() { checkNotSubtype("{{null f2} f1,null f2}","{{int f2} f1}"); }
	@Test public void test_3394() { checkNotSubtype("{{null f2} f1,null f2}","{{int f1} f2}"); }
	@Test public void test_3395() { checkNotSubtype("{{null f2} f1,null f2}","{{int f2} f2}"); }
	@Test public void test_3396() { checkNotSubtype("{{null f2} f1,null f2}","{{int f1} f1,int f2}"); }
	@Test public void test_3397() { checkNotSubtype("{{null f2} f1,null f2}","{{int f2} f1,int f2}"); }
	@Test public void test_3398() { checkNotSubtype("{{null f2} f1,null f2}","{{int f1} f2,int f3}"); }
	@Test public void test_3399() { checkNotSubtype("{{null f2} f1,null f2}","{{int f2} f2,int f3}"); }
	@Test public void test_3401() { checkNotSubtype("{{null f2} f1,null f2}","null"); }
	@Test public void test_3402() { checkNotSubtype("{{null f2} f1,null f2}","int"); }
	@Test public void test_3407() { checkNotSubtype("{{null f2} f1,null f2}","null"); }
	@Test public void test_3409() { checkNotSubtype("{{null f2} f1,null f2}","null"); }
	@Test public void test_3410() { checkNotSubtype("{{null f2} f1,null f2}","null|int"); }
	@Test public void test_3411() { checkNotSubtype("{{null f2} f1,null f2}","int"); }
	@Test public void test_3413() { checkNotSubtype("{{null f2} f1,null f2}","int|null"); }
	@Test public void test_3414() { checkNotSubtype("{{null f2} f1,null f2}","int"); }
	@Test public void test_3417() { checkNotSubtype("{{null f2} f1,null f2}","{null f1}|null"); }
	@Test public void test_3418() { checkNotSubtype("{{null f2} f1,null f2}","{null f2}|null"); }
	@Test public void test_3419() { checkNotSubtype("{{null f2} f1,null f2}","{int f1}|int"); }
	@Test public void test_3420() { checkNotSubtype("{{null f2} f1,null f2}","{int f2}|int"); }
	@Test public void test_3422() { checkNotSubtype("{{null f1} f2,null f3}","null"); }
	@Test public void test_3423() { checkNotSubtype("{{null f1} f2,null f3}","int"); }
	@Test public void test_3426() { checkNotSubtype("{{null f1} f2,null f3}","{null f1}"); }
	@Test public void test_3427() { checkNotSubtype("{{null f1} f2,null f3}","{null f2}"); }
	@Test public void test_3428() { checkNotSubtype("{{null f1} f2,null f3}","{int f1}"); }
	@Test public void test_3429() { checkNotSubtype("{{null f1} f2,null f3}","{int f2}"); }
	@Test public void test_3438() { checkNotSubtype("{{null f1} f2,null f3}","{null f1,null f2}"); }
	@Test public void test_3439() { checkNotSubtype("{{null f1} f2,null f3}","{null f2,null f3}"); }
	@Test public void test_3440() { checkNotSubtype("{{null f1} f2,null f3}","{null f1,int f2}"); }
	@Test public void test_3441() { checkNotSubtype("{{null f1} f2,null f3}","{null f2,int f3}"); }
	@Test public void test_3444() { checkNotSubtype("{{null f1} f2,null f3}","{int f1,null f2}"); }
	@Test public void test_3445() { checkNotSubtype("{{null f1} f2,null f3}","{int f2,null f3}"); }
	@Test public void test_3446() { checkNotSubtype("{{null f1} f2,null f3}","{int f1,int f2}"); }
	@Test public void test_3447() { checkNotSubtype("{{null f1} f2,null f3}","{int f2,int f3}"); }
	@Test public void test_3460() { checkNotSubtype("{{null f1} f2,null f3}","{{null f1} f1}"); }
	@Test public void test_3461() { checkNotSubtype("{{null f1} f2,null f3}","{{null f2} f1}"); }
	@Test public void test_3462() { checkNotSubtype("{{null f1} f2,null f3}","{{null f1} f2}"); }
	@Test public void test_3463() { checkNotSubtype("{{null f1} f2,null f3}","{{null f2} f2}"); }
	@Test public void test_3464() { checkNotSubtype("{{null f1} f2,null f3}","{{null f1} f1,null f2}"); }
	@Test public void test_3465() { checkNotSubtype("{{null f1} f2,null f3}","{{null f2} f1,null f2}"); }
	@Test public void test_3466() { checkIsSubtype("{{null f1} f2,null f3}","{{null f1} f2,null f3}"); }
	@Test public void test_3467() { checkNotSubtype("{{null f1} f2,null f3}","{{null f2} f2,null f3}"); }
	@Test public void test_3468() { checkNotSubtype("{{null f1} f2,null f3}","{{int f1} f1}"); }
	@Test public void test_3469() { checkNotSubtype("{{null f1} f2,null f3}","{{int f2} f1}"); }
	@Test public void test_3470() { checkNotSubtype("{{null f1} f2,null f3}","{{int f1} f2}"); }
	@Test public void test_3471() { checkNotSubtype("{{null f1} f2,null f3}","{{int f2} f2}"); }
	@Test public void test_3472() { checkNotSubtype("{{null f1} f2,null f3}","{{int f1} f1,int f2}"); }
	@Test public void test_3473() { checkNotSubtype("{{null f1} f2,null f3}","{{int f2} f1,int f2}"); }
	@Test public void test_3474() { checkNotSubtype("{{null f1} f2,null f3}","{{int f1} f2,int f3}"); }
	@Test public void test_3475() { checkNotSubtype("{{null f1} f2,null f3}","{{int f2} f2,int f3}"); }
	@Test public void test_3477() { checkNotSubtype("{{null f1} f2,null f3}","null"); }
	@Test public void test_3478() { checkNotSubtype("{{null f1} f2,null f3}","int"); }
	@Test public void test_3483() { checkNotSubtype("{{null f1} f2,null f3}","null"); }
	@Test public void test_3485() { checkNotSubtype("{{null f1} f2,null f3}","null"); }
	@Test public void test_3486() { checkNotSubtype("{{null f1} f2,null f3}","null|int"); }
	@Test public void test_3487() { checkNotSubtype("{{null f1} f2,null f3}","int"); }
	@Test public void test_3489() { checkNotSubtype("{{null f1} f2,null f3}","int|null"); }
	@Test public void test_3490() { checkNotSubtype("{{null f1} f2,null f3}","int"); }
	@Test public void test_3493() { checkNotSubtype("{{null f1} f2,null f3}","{null f1}|null"); }
	@Test public void test_3494() { checkNotSubtype("{{null f1} f2,null f3}","{null f2}|null"); }
	@Test public void test_3495() { checkNotSubtype("{{null f1} f2,null f3}","{int f1}|int"); }
	@Test public void test_3496() { checkNotSubtype("{{null f1} f2,null f3}","{int f2}|int"); }
	@Test public void test_3498() { checkNotSubtype("{{null f2} f2,null f3}","null"); }
	@Test public void test_3499() { checkNotSubtype("{{null f2} f2,null f3}","int"); }
	@Test public void test_3502() { checkNotSubtype("{{null f2} f2,null f3}","{null f1}"); }
	@Test public void test_3503() { checkNotSubtype("{{null f2} f2,null f3}","{null f2}"); }
	@Test public void test_3504() { checkNotSubtype("{{null f2} f2,null f3}","{int f1}"); }
	@Test public void test_3505() { checkNotSubtype("{{null f2} f2,null f3}","{int f2}"); }
	@Test public void test_3514() { checkNotSubtype("{{null f2} f2,null f3}","{null f1,null f2}"); }
	@Test public void test_3515() { checkNotSubtype("{{null f2} f2,null f3}","{null f2,null f3}"); }
	@Test public void test_3516() { checkNotSubtype("{{null f2} f2,null f3}","{null f1,int f2}"); }
	@Test public void test_3517() { checkNotSubtype("{{null f2} f2,null f3}","{null f2,int f3}"); }
	@Test public void test_3520() { checkNotSubtype("{{null f2} f2,null f3}","{int f1,null f2}"); }
	@Test public void test_3521() { checkNotSubtype("{{null f2} f2,null f3}","{int f2,null f3}"); }
	@Test public void test_3522() { checkNotSubtype("{{null f2} f2,null f3}","{int f1,int f2}"); }
	@Test public void test_3523() { checkNotSubtype("{{null f2} f2,null f3}","{int f2,int f3}"); }
	@Test public void test_3536() { checkNotSubtype("{{null f2} f2,null f3}","{{null f1} f1}"); }
	@Test public void test_3537() { checkNotSubtype("{{null f2} f2,null f3}","{{null f2} f1}"); }
	@Test public void test_3538() { checkNotSubtype("{{null f2} f2,null f3}","{{null f1} f2}"); }
	@Test public void test_3539() { checkNotSubtype("{{null f2} f2,null f3}","{{null f2} f2}"); }
	@Test public void test_3540() { checkNotSubtype("{{null f2} f2,null f3}","{{null f1} f1,null f2}"); }
	@Test public void test_3541() { checkNotSubtype("{{null f2} f2,null f3}","{{null f2} f1,null f2}"); }
	@Test public void test_3542() { checkNotSubtype("{{null f2} f2,null f3}","{{null f1} f2,null f3}"); }
	@Test public void test_3543() { checkIsSubtype("{{null f2} f2,null f3}","{{null f2} f2,null f3}"); }
	@Test public void test_3544() { checkNotSubtype("{{null f2} f2,null f3}","{{int f1} f1}"); }
	@Test public void test_3545() { checkNotSubtype("{{null f2} f2,null f3}","{{int f2} f1}"); }
	@Test public void test_3546() { checkNotSubtype("{{null f2} f2,null f3}","{{int f1} f2}"); }
	@Test public void test_3547() { checkNotSubtype("{{null f2} f2,null f3}","{{int f2} f2}"); }
	@Test public void test_3548() { checkNotSubtype("{{null f2} f2,null f3}","{{int f1} f1,int f2}"); }
	@Test public void test_3549() { checkNotSubtype("{{null f2} f2,null f3}","{{int f2} f1,int f2}"); }
	@Test public void test_3550() { checkNotSubtype("{{null f2} f2,null f3}","{{int f1} f2,int f3}"); }
	@Test public void test_3551() { checkNotSubtype("{{null f2} f2,null f3}","{{int f2} f2,int f3}"); }
	@Test public void test_3553() { checkNotSubtype("{{null f2} f2,null f3}","null"); }
	@Test public void test_3554() { checkNotSubtype("{{null f2} f2,null f3}","int"); }
	@Test public void test_3559() { checkNotSubtype("{{null f2} f2,null f3}","null"); }
	@Test public void test_3561() { checkNotSubtype("{{null f2} f2,null f3}","null"); }
	@Test public void test_3562() { checkNotSubtype("{{null f2} f2,null f3}","null|int"); }
	@Test public void test_3563() { checkNotSubtype("{{null f2} f2,null f3}","int"); }
	@Test public void test_3565() { checkNotSubtype("{{null f2} f2,null f3}","int|null"); }
	@Test public void test_3566() { checkNotSubtype("{{null f2} f2,null f3}","int"); }
	@Test public void test_3569() { checkNotSubtype("{{null f2} f2,null f3}","{null f1}|null"); }
	@Test public void test_3570() { checkNotSubtype("{{null f2} f2,null f3}","{null f2}|null"); }
	@Test public void test_3571() { checkNotSubtype("{{null f2} f2,null f3}","{int f1}|int"); }
	@Test public void test_3572() { checkNotSubtype("{{null f2} f2,null f3}","{int f2}|int"); }
	@Test public void test_3574() { checkNotSubtype("{{int f1} f1}","null"); }
	@Test public void test_3575() { checkNotSubtype("{{int f1} f1}","int"); }
	@Test public void test_3578() { checkNotSubtype("{{int f1} f1}","{null f1}"); }
	@Test public void test_3579() { checkNotSubtype("{{int f1} f1}","{null f2}"); }
	@Test public void test_3580() { checkNotSubtype("{{int f1} f1}","{int f1}"); }
	@Test public void test_3581() { checkNotSubtype("{{int f1} f1}","{int f2}"); }
	@Test public void test_3590() { checkNotSubtype("{{int f1} f1}","{null f1,null f2}"); }
	@Test public void test_3591() { checkNotSubtype("{{int f1} f1}","{null f2,null f3}"); }
	@Test public void test_3592() { checkNotSubtype("{{int f1} f1}","{null f1,int f2}"); }
	@Test public void test_3593() { checkNotSubtype("{{int f1} f1}","{null f2,int f3}"); }
	@Test public void test_3596() { checkNotSubtype("{{int f1} f1}","{int f1,null f2}"); }
	@Test public void test_3597() { checkNotSubtype("{{int f1} f1}","{int f2,null f3}"); }
	@Test public void test_3598() { checkNotSubtype("{{int f1} f1}","{int f1,int f2}"); }
	@Test public void test_3599() { checkNotSubtype("{{int f1} f1}","{int f2,int f3}"); }
	@Test public void test_3612() { checkNotSubtype("{{int f1} f1}","{{null f1} f1}"); }
	@Test public void test_3613() { checkNotSubtype("{{int f1} f1}","{{null f2} f1}"); }
	@Test public void test_3614() { checkNotSubtype("{{int f1} f1}","{{null f1} f2}"); }
	@Test public void test_3615() { checkNotSubtype("{{int f1} f1}","{{null f2} f2}"); }
	@Test public void test_3616() { checkNotSubtype("{{int f1} f1}","{{null f1} f1,null f2}"); }
	@Test public void test_3617() { checkNotSubtype("{{int f1} f1}","{{null f2} f1,null f2}"); }
	@Test public void test_3618() { checkNotSubtype("{{int f1} f1}","{{null f1} f2,null f3}"); }
	@Test public void test_3619() { checkNotSubtype("{{int f1} f1}","{{null f2} f2,null f3}"); }
	@Test public void test_3620() { checkIsSubtype("{{int f1} f1}","{{int f1} f1}"); }
	@Test public void test_3621() { checkNotSubtype("{{int f1} f1}","{{int f2} f1}"); }
	@Test public void test_3622() { checkNotSubtype("{{int f1} f1}","{{int f1} f2}"); }
	@Test public void test_3623() { checkNotSubtype("{{int f1} f1}","{{int f2} f2}"); }
	@Test public void test_3624() { checkNotSubtype("{{int f1} f1}","{{int f1} f1,int f2}"); }
	@Test public void test_3625() { checkNotSubtype("{{int f1} f1}","{{int f2} f1,int f2}"); }
	@Test public void test_3626() { checkNotSubtype("{{int f1} f1}","{{int f1} f2,int f3}"); }
	@Test public void test_3627() { checkNotSubtype("{{int f1} f1}","{{int f2} f2,int f3}"); }
	@Test public void test_3629() { checkNotSubtype("{{int f1} f1}","null"); }
	@Test public void test_3630() { checkNotSubtype("{{int f1} f1}","int"); }
	@Test public void test_3635() { checkNotSubtype("{{int f1} f1}","null"); }
	@Test public void test_3637() { checkNotSubtype("{{int f1} f1}","null"); }
	@Test public void test_3638() { checkNotSubtype("{{int f1} f1}","null|int"); }
	@Test public void test_3639() { checkNotSubtype("{{int f1} f1}","int"); }
	@Test public void test_3641() { checkNotSubtype("{{int f1} f1}","int|null"); }
	@Test public void test_3642() { checkNotSubtype("{{int f1} f1}","int"); }
	@Test public void test_3645() { checkNotSubtype("{{int f1} f1}","{null f1}|null"); }
	@Test public void test_3646() { checkNotSubtype("{{int f1} f1}","{null f2}|null"); }
	@Test public void test_3647() { checkNotSubtype("{{int f1} f1}","{int f1}|int"); }
	@Test public void test_3648() { checkNotSubtype("{{int f1} f1}","{int f2}|int"); }
	@Test public void test_3650() { checkNotSubtype("{{int f2} f1}","null"); }
	@Test public void test_3651() { checkNotSubtype("{{int f2} f1}","int"); }
	@Test public void test_3654() { checkNotSubtype("{{int f2} f1}","{null f1}"); }
	@Test public void test_3655() { checkNotSubtype("{{int f2} f1}","{null f2}"); }
	@Test public void test_3656() { checkNotSubtype("{{int f2} f1}","{int f1}"); }
	@Test public void test_3657() { checkNotSubtype("{{int f2} f1}","{int f2}"); }
	@Test public void test_3666() { checkNotSubtype("{{int f2} f1}","{null f1,null f2}"); }
	@Test public void test_3667() { checkNotSubtype("{{int f2} f1}","{null f2,null f3}"); }
	@Test public void test_3668() { checkNotSubtype("{{int f2} f1}","{null f1,int f2}"); }
	@Test public void test_3669() { checkNotSubtype("{{int f2} f1}","{null f2,int f3}"); }
	@Test public void test_3672() { checkNotSubtype("{{int f2} f1}","{int f1,null f2}"); }
	@Test public void test_3673() { checkNotSubtype("{{int f2} f1}","{int f2,null f3}"); }
	@Test public void test_3674() { checkNotSubtype("{{int f2} f1}","{int f1,int f2}"); }
	@Test public void test_3675() { checkNotSubtype("{{int f2} f1}","{int f2,int f3}"); }
	@Test public void test_3688() { checkNotSubtype("{{int f2} f1}","{{null f1} f1}"); }
	@Test public void test_3689() { checkNotSubtype("{{int f2} f1}","{{null f2} f1}"); }
	@Test public void test_3690() { checkNotSubtype("{{int f2} f1}","{{null f1} f2}"); }
	@Test public void test_3691() { checkNotSubtype("{{int f2} f1}","{{null f2} f2}"); }
	@Test public void test_3692() { checkNotSubtype("{{int f2} f1}","{{null f1} f1,null f2}"); }
	@Test public void test_3693() { checkNotSubtype("{{int f2} f1}","{{null f2} f1,null f2}"); }
	@Test public void test_3694() { checkNotSubtype("{{int f2} f1}","{{null f1} f2,null f3}"); }
	@Test public void test_3695() { checkNotSubtype("{{int f2} f1}","{{null f2} f2,null f3}"); }
	@Test public void test_3696() { checkNotSubtype("{{int f2} f1}","{{int f1} f1}"); }
	@Test public void test_3697() { checkIsSubtype("{{int f2} f1}","{{int f2} f1}"); }
	@Test public void test_3698() { checkNotSubtype("{{int f2} f1}","{{int f1} f2}"); }
	@Test public void test_3699() { checkNotSubtype("{{int f2} f1}","{{int f2} f2}"); }
	@Test public void test_3700() { checkNotSubtype("{{int f2} f1}","{{int f1} f1,int f2}"); }
	@Test public void test_3701() { checkNotSubtype("{{int f2} f1}","{{int f2} f1,int f2}"); }
	@Test public void test_3702() { checkNotSubtype("{{int f2} f1}","{{int f1} f2,int f3}"); }
	@Test public void test_3703() { checkNotSubtype("{{int f2} f1}","{{int f2} f2,int f3}"); }
	@Test public void test_3705() { checkNotSubtype("{{int f2} f1}","null"); }
	@Test public void test_3706() { checkNotSubtype("{{int f2} f1}","int"); }
	@Test public void test_3711() { checkNotSubtype("{{int f2} f1}","null"); }
	@Test public void test_3713() { checkNotSubtype("{{int f2} f1}","null"); }
	@Test public void test_3714() { checkNotSubtype("{{int f2} f1}","null|int"); }
	@Test public void test_3715() { checkNotSubtype("{{int f2} f1}","int"); }
	@Test public void test_3717() { checkNotSubtype("{{int f2} f1}","int|null"); }
	@Test public void test_3718() { checkNotSubtype("{{int f2} f1}","int"); }
	@Test public void test_3721() { checkNotSubtype("{{int f2} f1}","{null f1}|null"); }
	@Test public void test_3722() { checkNotSubtype("{{int f2} f1}","{null f2}|null"); }
	@Test public void test_3723() { checkNotSubtype("{{int f2} f1}","{int f1}|int"); }
	@Test public void test_3724() { checkNotSubtype("{{int f2} f1}","{int f2}|int"); }
	@Test public void test_3726() { checkNotSubtype("{{int f1} f2}","null"); }
	@Test public void test_3727() { checkNotSubtype("{{int f1} f2}","int"); }
	@Test public void test_3730() { checkNotSubtype("{{int f1} f2}","{null f1}"); }
	@Test public void test_3731() { checkNotSubtype("{{int f1} f2}","{null f2}"); }
	@Test public void test_3732() { checkNotSubtype("{{int f1} f2}","{int f1}"); }
	@Test public void test_3733() { checkNotSubtype("{{int f1} f2}","{int f2}"); }
	@Test public void test_3742() { checkNotSubtype("{{int f1} f2}","{null f1,null f2}"); }
	@Test public void test_3743() { checkNotSubtype("{{int f1} f2}","{null f2,null f3}"); }
	@Test public void test_3744() { checkNotSubtype("{{int f1} f2}","{null f1,int f2}"); }
	@Test public void test_3745() { checkNotSubtype("{{int f1} f2}","{null f2,int f3}"); }
	@Test public void test_3748() { checkNotSubtype("{{int f1} f2}","{int f1,null f2}"); }
	@Test public void test_3749() { checkNotSubtype("{{int f1} f2}","{int f2,null f3}"); }
	@Test public void test_3750() { checkNotSubtype("{{int f1} f2}","{int f1,int f2}"); }
	@Test public void test_3751() { checkNotSubtype("{{int f1} f2}","{int f2,int f3}"); }
	@Test public void test_3764() { checkNotSubtype("{{int f1} f2}","{{null f1} f1}"); }
	@Test public void test_3765() { checkNotSubtype("{{int f1} f2}","{{null f2} f1}"); }
	@Test public void test_3766() { checkNotSubtype("{{int f1} f2}","{{null f1} f2}"); }
	@Test public void test_3767() { checkNotSubtype("{{int f1} f2}","{{null f2} f2}"); }
	@Test public void test_3768() { checkNotSubtype("{{int f1} f2}","{{null f1} f1,null f2}"); }
	@Test public void test_3769() { checkNotSubtype("{{int f1} f2}","{{null f2} f1,null f2}"); }
	@Test public void test_3770() { checkNotSubtype("{{int f1} f2}","{{null f1} f2,null f3}"); }
	@Test public void test_3771() { checkNotSubtype("{{int f1} f2}","{{null f2} f2,null f3}"); }
	@Test public void test_3772() { checkNotSubtype("{{int f1} f2}","{{int f1} f1}"); }
	@Test public void test_3773() { checkNotSubtype("{{int f1} f2}","{{int f2} f1}"); }
	@Test public void test_3774() { checkIsSubtype("{{int f1} f2}","{{int f1} f2}"); }
	@Test public void test_3775() { checkNotSubtype("{{int f1} f2}","{{int f2} f2}"); }
	@Test public void test_3776() { checkNotSubtype("{{int f1} f2}","{{int f1} f1,int f2}"); }
	@Test public void test_3777() { checkNotSubtype("{{int f1} f2}","{{int f2} f1,int f2}"); }
	@Test public void test_3778() { checkNotSubtype("{{int f1} f2}","{{int f1} f2,int f3}"); }
	@Test public void test_3779() { checkNotSubtype("{{int f1} f2}","{{int f2} f2,int f3}"); }
	@Test public void test_3781() { checkNotSubtype("{{int f1} f2}","null"); }
	@Test public void test_3782() { checkNotSubtype("{{int f1} f2}","int"); }
	@Test public void test_3787() { checkNotSubtype("{{int f1} f2}","null"); }
	@Test public void test_3789() { checkNotSubtype("{{int f1} f2}","null"); }
	@Test public void test_3790() { checkNotSubtype("{{int f1} f2}","null|int"); }
	@Test public void test_3791() { checkNotSubtype("{{int f1} f2}","int"); }
	@Test public void test_3793() { checkNotSubtype("{{int f1} f2}","int|null"); }
	@Test public void test_3794() { checkNotSubtype("{{int f1} f2}","int"); }
	@Test public void test_3797() { checkNotSubtype("{{int f1} f2}","{null f1}|null"); }
	@Test public void test_3798() { checkNotSubtype("{{int f1} f2}","{null f2}|null"); }
	@Test public void test_3799() { checkNotSubtype("{{int f1} f2}","{int f1}|int"); }
	@Test public void test_3800() { checkNotSubtype("{{int f1} f2}","{int f2}|int"); }
	@Test public void test_3802() { checkNotSubtype("{{int f2} f2}","null"); }
	@Test public void test_3803() { checkNotSubtype("{{int f2} f2}","int"); }
	@Test public void test_3806() { checkNotSubtype("{{int f2} f2}","{null f1}"); }
	@Test public void test_3807() { checkNotSubtype("{{int f2} f2}","{null f2}"); }
	@Test public void test_3808() { checkNotSubtype("{{int f2} f2}","{int f1}"); }
	@Test public void test_3809() { checkNotSubtype("{{int f2} f2}","{int f2}"); }
	@Test public void test_3818() { checkNotSubtype("{{int f2} f2}","{null f1,null f2}"); }
	@Test public void test_3819() { checkNotSubtype("{{int f2} f2}","{null f2,null f3}"); }
	@Test public void test_3820() { checkNotSubtype("{{int f2} f2}","{null f1,int f2}"); }
	@Test public void test_3821() { checkNotSubtype("{{int f2} f2}","{null f2,int f3}"); }
	@Test public void test_3824() { checkNotSubtype("{{int f2} f2}","{int f1,null f2}"); }
	@Test public void test_3825() { checkNotSubtype("{{int f2} f2}","{int f2,null f3}"); }
	@Test public void test_3826() { checkNotSubtype("{{int f2} f2}","{int f1,int f2}"); }
	@Test public void test_3827() { checkNotSubtype("{{int f2} f2}","{int f2,int f3}"); }
	@Test public void test_3840() { checkNotSubtype("{{int f2} f2}","{{null f1} f1}"); }
	@Test public void test_3841() { checkNotSubtype("{{int f2} f2}","{{null f2} f1}"); }
	@Test public void test_3842() { checkNotSubtype("{{int f2} f2}","{{null f1} f2}"); }
	@Test public void test_3843() { checkNotSubtype("{{int f2} f2}","{{null f2} f2}"); }
	@Test public void test_3844() { checkNotSubtype("{{int f2} f2}","{{null f1} f1,null f2}"); }
	@Test public void test_3845() { checkNotSubtype("{{int f2} f2}","{{null f2} f1,null f2}"); }
	@Test public void test_3846() { checkNotSubtype("{{int f2} f2}","{{null f1} f2,null f3}"); }
	@Test public void test_3847() { checkNotSubtype("{{int f2} f2}","{{null f2} f2,null f3}"); }
	@Test public void test_3848() { checkNotSubtype("{{int f2} f2}","{{int f1} f1}"); }
	@Test public void test_3849() { checkNotSubtype("{{int f2} f2}","{{int f2} f1}"); }
	@Test public void test_3850() { checkNotSubtype("{{int f2} f2}","{{int f1} f2}"); }
	@Test public void test_3851() { checkIsSubtype("{{int f2} f2}","{{int f2} f2}"); }
	@Test public void test_3852() { checkNotSubtype("{{int f2} f2}","{{int f1} f1,int f2}"); }
	@Test public void test_3853() { checkNotSubtype("{{int f2} f2}","{{int f2} f1,int f2}"); }
	@Test public void test_3854() { checkNotSubtype("{{int f2} f2}","{{int f1} f2,int f3}"); }
	@Test public void test_3855() { checkNotSubtype("{{int f2} f2}","{{int f2} f2,int f3}"); }
	@Test public void test_3857() { checkNotSubtype("{{int f2} f2}","null"); }
	@Test public void test_3858() { checkNotSubtype("{{int f2} f2}","int"); }
	@Test public void test_3863() { checkNotSubtype("{{int f2} f2}","null"); }
	@Test public void test_3865() { checkNotSubtype("{{int f2} f2}","null"); }
	@Test public void test_3866() { checkNotSubtype("{{int f2} f2}","null|int"); }
	@Test public void test_3867() { checkNotSubtype("{{int f2} f2}","int"); }
	@Test public void test_3869() { checkNotSubtype("{{int f2} f2}","int|null"); }
	@Test public void test_3870() { checkNotSubtype("{{int f2} f2}","int"); }
	@Test public void test_3873() { checkNotSubtype("{{int f2} f2}","{null f1}|null"); }
	@Test public void test_3874() { checkNotSubtype("{{int f2} f2}","{null f2}|null"); }
	@Test public void test_3875() { checkNotSubtype("{{int f2} f2}","{int f1}|int"); }
	@Test public void test_3876() { checkNotSubtype("{{int f2} f2}","{int f2}|int"); }
	@Test public void test_3878() { checkNotSubtype("{{int f1} f1,int f2}","null"); }
	@Test public void test_3879() { checkNotSubtype("{{int f1} f1,int f2}","int"); }
	@Test public void test_3882() { checkNotSubtype("{{int f1} f1,int f2}","{null f1}"); }
	@Test public void test_3883() { checkNotSubtype("{{int f1} f1,int f2}","{null f2}"); }
	@Test public void test_3884() { checkNotSubtype("{{int f1} f1,int f2}","{int f1}"); }
	@Test public void test_3885() { checkNotSubtype("{{int f1} f1,int f2}","{int f2}"); }
	@Test public void test_3894() { checkNotSubtype("{{int f1} f1,int f2}","{null f1,null f2}"); }
	@Test public void test_3895() { checkNotSubtype("{{int f1} f1,int f2}","{null f2,null f3}"); }
	@Test public void test_3896() { checkNotSubtype("{{int f1} f1,int f2}","{null f1,int f2}"); }
	@Test public void test_3897() { checkNotSubtype("{{int f1} f1,int f2}","{null f2,int f3}"); }
	@Test public void test_3900() { checkNotSubtype("{{int f1} f1,int f2}","{int f1,null f2}"); }
	@Test public void test_3901() { checkNotSubtype("{{int f1} f1,int f2}","{int f2,null f3}"); }
	@Test public void test_3902() { checkNotSubtype("{{int f1} f1,int f2}","{int f1,int f2}"); }
	@Test public void test_3903() { checkNotSubtype("{{int f1} f1,int f2}","{int f2,int f3}"); }
	@Test public void test_3916() { checkNotSubtype("{{int f1} f1,int f2}","{{null f1} f1}"); }
	@Test public void test_3917() { checkNotSubtype("{{int f1} f1,int f2}","{{null f2} f1}"); }
	@Test public void test_3918() { checkNotSubtype("{{int f1} f1,int f2}","{{null f1} f2}"); }
	@Test public void test_3919() { checkNotSubtype("{{int f1} f1,int f2}","{{null f2} f2}"); }
	@Test public void test_3920() { checkNotSubtype("{{int f1} f1,int f2}","{{null f1} f1,null f2}"); }
	@Test public void test_3921() { checkNotSubtype("{{int f1} f1,int f2}","{{null f2} f1,null f2}"); }
	@Test public void test_3922() { checkNotSubtype("{{int f1} f1,int f2}","{{null f1} f2,null f3}"); }
	@Test public void test_3923() { checkNotSubtype("{{int f1} f1,int f2}","{{null f2} f2,null f3}"); }
	@Test public void test_3924() { checkNotSubtype("{{int f1} f1,int f2}","{{int f1} f1}"); }
	@Test public void test_3925() { checkNotSubtype("{{int f1} f1,int f2}","{{int f2} f1}"); }
	@Test public void test_3926() { checkNotSubtype("{{int f1} f1,int f2}","{{int f1} f2}"); }
	@Test public void test_3927() { checkNotSubtype("{{int f1} f1,int f2}","{{int f2} f2}"); }
	@Test public void test_3928() { checkIsSubtype("{{int f1} f1,int f2}","{{int f1} f1,int f2}"); }
	@Test public void test_3929() { checkNotSubtype("{{int f1} f1,int f2}","{{int f2} f1,int f2}"); }
	@Test public void test_3930() { checkNotSubtype("{{int f1} f1,int f2}","{{int f1} f2,int f3}"); }
	@Test public void test_3931() { checkNotSubtype("{{int f1} f1,int f2}","{{int f2} f2,int f3}"); }
	@Test public void test_3933() { checkNotSubtype("{{int f1} f1,int f2}","null"); }
	@Test public void test_3934() { checkNotSubtype("{{int f1} f1,int f2}","int"); }
	@Test public void test_3939() { checkNotSubtype("{{int f1} f1,int f2}","null"); }
	@Test public void test_3941() { checkNotSubtype("{{int f1} f1,int f2}","null"); }
	@Test public void test_3942() { checkNotSubtype("{{int f1} f1,int f2}","null|int"); }
	@Test public void test_3943() { checkNotSubtype("{{int f1} f1,int f2}","int"); }
	@Test public void test_3945() { checkNotSubtype("{{int f1} f1,int f2}","int|null"); }
	@Test public void test_3946() { checkNotSubtype("{{int f1} f1,int f2}","int"); }
	@Test public void test_3949() { checkNotSubtype("{{int f1} f1,int f2}","{null f1}|null"); }
	@Test public void test_3950() { checkNotSubtype("{{int f1} f1,int f2}","{null f2}|null"); }
	@Test public void test_3951() { checkNotSubtype("{{int f1} f1,int f2}","{int f1}|int"); }
	@Test public void test_3952() { checkNotSubtype("{{int f1} f1,int f2}","{int f2}|int"); }
	@Test public void test_3954() { checkNotSubtype("{{int f2} f1,int f2}","null"); }
	@Test public void test_3955() { checkNotSubtype("{{int f2} f1,int f2}","int"); }
	@Test public void test_3958() { checkNotSubtype("{{int f2} f1,int f2}","{null f1}"); }
	@Test public void test_3959() { checkNotSubtype("{{int f2} f1,int f2}","{null f2}"); }
	@Test public void test_3960() { checkNotSubtype("{{int f2} f1,int f2}","{int f1}"); }
	@Test public void test_3961() { checkNotSubtype("{{int f2} f1,int f2}","{int f2}"); }
	@Test public void test_3970() { checkNotSubtype("{{int f2} f1,int f2}","{null f1,null f2}"); }
	@Test public void test_3971() { checkNotSubtype("{{int f2} f1,int f2}","{null f2,null f3}"); }
	@Test public void test_3972() { checkNotSubtype("{{int f2} f1,int f2}","{null f1,int f2}"); }
	@Test public void test_3973() { checkNotSubtype("{{int f2} f1,int f2}","{null f2,int f3}"); }
	@Test public void test_3976() { checkNotSubtype("{{int f2} f1,int f2}","{int f1,null f2}"); }
	@Test public void test_3977() { checkNotSubtype("{{int f2} f1,int f2}","{int f2,null f3}"); }
	@Test public void test_3978() { checkNotSubtype("{{int f2} f1,int f2}","{int f1,int f2}"); }
	@Test public void test_3979() { checkNotSubtype("{{int f2} f1,int f2}","{int f2,int f3}"); }
	@Test public void test_3992() { checkNotSubtype("{{int f2} f1,int f2}","{{null f1} f1}"); }
	@Test public void test_3993() { checkNotSubtype("{{int f2} f1,int f2}","{{null f2} f1}"); }
	@Test public void test_3994() { checkNotSubtype("{{int f2} f1,int f2}","{{null f1} f2}"); }
	@Test public void test_3995() { checkNotSubtype("{{int f2} f1,int f2}","{{null f2} f2}"); }
	@Test public void test_3996() { checkNotSubtype("{{int f2} f1,int f2}","{{null f1} f1,null f2}"); }
	@Test public void test_3997() { checkNotSubtype("{{int f2} f1,int f2}","{{null f2} f1,null f2}"); }
	@Test public void test_3998() { checkNotSubtype("{{int f2} f1,int f2}","{{null f1} f2,null f3}"); }
	@Test public void test_3999() { checkNotSubtype("{{int f2} f1,int f2}","{{null f2} f2,null f3}"); }
	@Test public void test_4000() { checkNotSubtype("{{int f2} f1,int f2}","{{int f1} f1}"); }
	@Test public void test_4001() { checkNotSubtype("{{int f2} f1,int f2}","{{int f2} f1}"); }
	@Test public void test_4002() { checkNotSubtype("{{int f2} f1,int f2}","{{int f1} f2}"); }
	@Test public void test_4003() { checkNotSubtype("{{int f2} f1,int f2}","{{int f2} f2}"); }
	@Test public void test_4004() { checkNotSubtype("{{int f2} f1,int f2}","{{int f1} f1,int f2}"); }
	@Test public void test_4005() { checkIsSubtype("{{int f2} f1,int f2}","{{int f2} f1,int f2}"); }
	@Test public void test_4006() { checkNotSubtype("{{int f2} f1,int f2}","{{int f1} f2,int f3}"); }
	@Test public void test_4007() { checkNotSubtype("{{int f2} f1,int f2}","{{int f2} f2,int f3}"); }
	@Test public void test_4009() { checkNotSubtype("{{int f2} f1,int f2}","null"); }
	@Test public void test_4010() { checkNotSubtype("{{int f2} f1,int f2}","int"); }
	@Test public void test_4015() { checkNotSubtype("{{int f2} f1,int f2}","null"); }
	@Test public void test_4017() { checkNotSubtype("{{int f2} f1,int f2}","null"); }
	@Test public void test_4018() { checkNotSubtype("{{int f2} f1,int f2}","null|int"); }
	@Test public void test_4019() { checkNotSubtype("{{int f2} f1,int f2}","int"); }
	@Test public void test_4021() { checkNotSubtype("{{int f2} f1,int f2}","int|null"); }
	@Test public void test_4022() { checkNotSubtype("{{int f2} f1,int f2}","int"); }
	@Test public void test_4025() { checkNotSubtype("{{int f2} f1,int f2}","{null f1}|null"); }
	@Test public void test_4026() { checkNotSubtype("{{int f2} f1,int f2}","{null f2}|null"); }
	@Test public void test_4027() { checkNotSubtype("{{int f2} f1,int f2}","{int f1}|int"); }
	@Test public void test_4028() { checkNotSubtype("{{int f2} f1,int f2}","{int f2}|int"); }
	@Test public void test_4030() { checkNotSubtype("{{int f1} f2,int f3}","null"); }
	@Test public void test_4031() { checkNotSubtype("{{int f1} f2,int f3}","int"); }
	@Test public void test_4034() { checkNotSubtype("{{int f1} f2,int f3}","{null f1}"); }
	@Test public void test_4035() { checkNotSubtype("{{int f1} f2,int f3}","{null f2}"); }
	@Test public void test_4036() { checkNotSubtype("{{int f1} f2,int f3}","{int f1}"); }
	@Test public void test_4037() { checkNotSubtype("{{int f1} f2,int f3}","{int f2}"); }
	@Test public void test_4046() { checkNotSubtype("{{int f1} f2,int f3}","{null f1,null f2}"); }
	@Test public void test_4047() { checkNotSubtype("{{int f1} f2,int f3}","{null f2,null f3}"); }
	@Test public void test_4048() { checkNotSubtype("{{int f1} f2,int f3}","{null f1,int f2}"); }
	@Test public void test_4049() { checkNotSubtype("{{int f1} f2,int f3}","{null f2,int f3}"); }
	@Test public void test_4052() { checkNotSubtype("{{int f1} f2,int f3}","{int f1,null f2}"); }
	@Test public void test_4053() { checkNotSubtype("{{int f1} f2,int f3}","{int f2,null f3}"); }
	@Test public void test_4054() { checkNotSubtype("{{int f1} f2,int f3}","{int f1,int f2}"); }
	@Test public void test_4055() { checkNotSubtype("{{int f1} f2,int f3}","{int f2,int f3}"); }
	@Test public void test_4068() { checkNotSubtype("{{int f1} f2,int f3}","{{null f1} f1}"); }
	@Test public void test_4069() { checkNotSubtype("{{int f1} f2,int f3}","{{null f2} f1}"); }
	@Test public void test_4070() { checkNotSubtype("{{int f1} f2,int f3}","{{null f1} f2}"); }
	@Test public void test_4071() { checkNotSubtype("{{int f1} f2,int f3}","{{null f2} f2}"); }
	@Test public void test_4072() { checkNotSubtype("{{int f1} f2,int f3}","{{null f1} f1,null f2}"); }
	@Test public void test_4073() { checkNotSubtype("{{int f1} f2,int f3}","{{null f2} f1,null f2}"); }
	@Test public void test_4074() { checkNotSubtype("{{int f1} f2,int f3}","{{null f1} f2,null f3}"); }
	@Test public void test_4075() { checkNotSubtype("{{int f1} f2,int f3}","{{null f2} f2,null f3}"); }
	@Test public void test_4076() { checkNotSubtype("{{int f1} f2,int f3}","{{int f1} f1}"); }
	@Test public void test_4077() { checkNotSubtype("{{int f1} f2,int f3}","{{int f2} f1}"); }
	@Test public void test_4078() { checkNotSubtype("{{int f1} f2,int f3}","{{int f1} f2}"); }
	@Test public void test_4079() { checkNotSubtype("{{int f1} f2,int f3}","{{int f2} f2}"); }
	@Test public void test_4080() { checkNotSubtype("{{int f1} f2,int f3}","{{int f1} f1,int f2}"); }
	@Test public void test_4081() { checkNotSubtype("{{int f1} f2,int f3}","{{int f2} f1,int f2}"); }
	@Test public void test_4082() { checkIsSubtype("{{int f1} f2,int f3}","{{int f1} f2,int f3}"); }
	@Test public void test_4083() { checkNotSubtype("{{int f1} f2,int f3}","{{int f2} f2,int f3}"); }
	@Test public void test_4085() { checkNotSubtype("{{int f1} f2,int f3}","null"); }
	@Test public void test_4086() { checkNotSubtype("{{int f1} f2,int f3}","int"); }
	@Test public void test_4091() { checkNotSubtype("{{int f1} f2,int f3}","null"); }
	@Test public void test_4093() { checkNotSubtype("{{int f1} f2,int f3}","null"); }
	@Test public void test_4094() { checkNotSubtype("{{int f1} f2,int f3}","null|int"); }
	@Test public void test_4095() { checkNotSubtype("{{int f1} f2,int f3}","int"); }
	@Test public void test_4097() { checkNotSubtype("{{int f1} f2,int f3}","int|null"); }
	@Test public void test_4098() { checkNotSubtype("{{int f1} f2,int f3}","int"); }
	@Test public void test_4101() { checkNotSubtype("{{int f1} f2,int f3}","{null f1}|null"); }
	@Test public void test_4102() { checkNotSubtype("{{int f1} f2,int f3}","{null f2}|null"); }
	@Test public void test_4103() { checkNotSubtype("{{int f1} f2,int f3}","{int f1}|int"); }
	@Test public void test_4104() { checkNotSubtype("{{int f1} f2,int f3}","{int f2}|int"); }
	@Test public void test_4106() { checkNotSubtype("{{int f2} f2,int f3}","null"); }
	@Test public void test_4107() { checkNotSubtype("{{int f2} f2,int f3}","int"); }
	@Test public void test_4110() { checkNotSubtype("{{int f2} f2,int f3}","{null f1}"); }
	@Test public void test_4111() { checkNotSubtype("{{int f2} f2,int f3}","{null f2}"); }
	@Test public void test_4112() { checkNotSubtype("{{int f2} f2,int f3}","{int f1}"); }
	@Test public void test_4113() { checkNotSubtype("{{int f2} f2,int f3}","{int f2}"); }
	@Test public void test_4122() { checkNotSubtype("{{int f2} f2,int f3}","{null f1,null f2}"); }
	@Test public void test_4123() { checkNotSubtype("{{int f2} f2,int f3}","{null f2,null f3}"); }
	@Test public void test_4124() { checkNotSubtype("{{int f2} f2,int f3}","{null f1,int f2}"); }
	@Test public void test_4125() { checkNotSubtype("{{int f2} f2,int f3}","{null f2,int f3}"); }
	@Test public void test_4128() { checkNotSubtype("{{int f2} f2,int f3}","{int f1,null f2}"); }
	@Test public void test_4129() { checkNotSubtype("{{int f2} f2,int f3}","{int f2,null f3}"); }
	@Test public void test_4130() { checkNotSubtype("{{int f2} f2,int f3}","{int f1,int f2}"); }
	@Test public void test_4131() { checkNotSubtype("{{int f2} f2,int f3}","{int f2,int f3}"); }
	@Test public void test_4144() { checkNotSubtype("{{int f2} f2,int f3}","{{null f1} f1}"); }
	@Test public void test_4145() { checkNotSubtype("{{int f2} f2,int f3}","{{null f2} f1}"); }
	@Test public void test_4146() { checkNotSubtype("{{int f2} f2,int f3}","{{null f1} f2}"); }
	@Test public void test_4147() { checkNotSubtype("{{int f2} f2,int f3}","{{null f2} f2}"); }
	@Test public void test_4148() { checkNotSubtype("{{int f2} f2,int f3}","{{null f1} f1,null f2}"); }
	@Test public void test_4149() { checkNotSubtype("{{int f2} f2,int f3}","{{null f2} f1,null f2}"); }
	@Test public void test_4150() { checkNotSubtype("{{int f2} f2,int f3}","{{null f1} f2,null f3}"); }
	@Test public void test_4151() { checkNotSubtype("{{int f2} f2,int f3}","{{null f2} f2,null f3}"); }
	@Test public void test_4152() { checkNotSubtype("{{int f2} f2,int f3}","{{int f1} f1}"); }
	@Test public void test_4153() { checkNotSubtype("{{int f2} f2,int f3}","{{int f2} f1}"); }
	@Test public void test_4154() { checkNotSubtype("{{int f2} f2,int f3}","{{int f1} f2}"); }
	@Test public void test_4155() { checkNotSubtype("{{int f2} f2,int f3}","{{int f2} f2}"); }
	@Test public void test_4156() { checkNotSubtype("{{int f2} f2,int f3}","{{int f1} f1,int f2}"); }
	@Test public void test_4157() { checkNotSubtype("{{int f2} f2,int f3}","{{int f2} f1,int f2}"); }
	@Test public void test_4158() { checkNotSubtype("{{int f2} f2,int f3}","{{int f1} f2,int f3}"); }
	@Test public void test_4159() { checkIsSubtype("{{int f2} f2,int f3}","{{int f2} f2,int f3}"); }
	@Test public void test_4161() { checkNotSubtype("{{int f2} f2,int f3}","null"); }
	@Test public void test_4162() { checkNotSubtype("{{int f2} f2,int f3}","int"); }
	@Test public void test_4167() { checkNotSubtype("{{int f2} f2,int f3}","null"); }
	@Test public void test_4169() { checkNotSubtype("{{int f2} f2,int f3}","null"); }
	@Test public void test_4170() { checkNotSubtype("{{int f2} f2,int f3}","null|int"); }
	@Test public void test_4171() { checkNotSubtype("{{int f2} f2,int f3}","int"); }
	@Test public void test_4173() { checkNotSubtype("{{int f2} f2,int f3}","int|null"); }
	@Test public void test_4174() { checkNotSubtype("{{int f2} f2,int f3}","int"); }
	@Test public void test_4177() { checkNotSubtype("{{int f2} f2,int f3}","{null f1}|null"); }
	@Test public void test_4178() { checkNotSubtype("{{int f2} f2,int f3}","{null f2}|null"); }
	@Test public void test_4179() { checkNotSubtype("{{int f2} f2,int f3}","{int f1}|int"); }
	@Test public void test_4180() { checkNotSubtype("{{int f2} f2,int f3}","{int f2}|int"); }
	@Test public void test_4258() { checkIsSubtype("null","null"); }
	@Test public void test_4259() { checkNotSubtype("null","int"); }
	@Test public void test_4262() { checkNotSubtype("null","{null f1}"); }
	@Test public void test_4263() { checkNotSubtype("null","{null f2}"); }
	@Test public void test_4264() { checkNotSubtype("null","{int f1}"); }
	@Test public void test_4265() { checkNotSubtype("null","{int f2}"); }
	@Test public void test_4274() { checkNotSubtype("null","{null f1,null f2}"); }
	@Test public void test_4275() { checkNotSubtype("null","{null f2,null f3}"); }
	@Test public void test_4276() { checkNotSubtype("null","{null f1,int f2}"); }
	@Test public void test_4277() { checkNotSubtype("null","{null f2,int f3}"); }
	@Test public void test_4280() { checkNotSubtype("null","{int f1,null f2}"); }
	@Test public void test_4281() { checkNotSubtype("null","{int f2,null f3}"); }
	@Test public void test_4282() { checkNotSubtype("null","{int f1,int f2}"); }
	@Test public void test_4283() { checkNotSubtype("null","{int f2,int f3}"); }
	@Test public void test_4296() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test public void test_4297() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test public void test_4298() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test public void test_4299() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test public void test_4300() { checkNotSubtype("null","{{null f1} f1,null f2}"); }
	@Test public void test_4301() { checkNotSubtype("null","{{null f2} f1,null f2}"); }
	@Test public void test_4302() { checkNotSubtype("null","{{null f1} f2,null f3}"); }
	@Test public void test_4303() { checkNotSubtype("null","{{null f2} f2,null f3}"); }
	@Test public void test_4304() { checkNotSubtype("null","{{int f1} f1}"); }
	@Test public void test_4305() { checkNotSubtype("null","{{int f2} f1}"); }
	@Test public void test_4306() { checkNotSubtype("null","{{int f1} f2}"); }
	@Test public void test_4307() { checkNotSubtype("null","{{int f2} f2}"); }
	@Test public void test_4308() { checkNotSubtype("null","{{int f1} f1,int f2}"); }
	@Test public void test_4309() { checkNotSubtype("null","{{int f2} f1,int f2}"); }
	@Test public void test_4310() { checkNotSubtype("null","{{int f1} f2,int f3}"); }
	@Test public void test_4311() { checkNotSubtype("null","{{int f2} f2,int f3}"); }
	@Test public void test_4313() { checkIsSubtype("null","null"); }
	@Test public void test_4314() { checkNotSubtype("null","int"); }
	@Test public void test_4319() { checkIsSubtype("null","null"); }
	@Test public void test_4321() { checkIsSubtype("null","null"); }
	@Test public void test_4322() { checkNotSubtype("null","null|int"); }
	@Test public void test_4323() { checkNotSubtype("null","int"); }
	@Test public void test_4325() { checkNotSubtype("null","int|null"); }
	@Test public void test_4326() { checkNotSubtype("null","int"); }
	@Test public void test_4329() { checkNotSubtype("null","{null f1}|null"); }
	@Test public void test_4330() { checkNotSubtype("null","{null f2}|null"); }
	@Test public void test_4331() { checkNotSubtype("null","{int f1}|int"); }
	@Test public void test_4332() { checkNotSubtype("null","{int f2}|int"); }
	@Test public void test_4334() { checkNotSubtype("int","null"); }
	@Test public void test_4335() { checkIsSubtype("int","int"); }
	@Test public void test_4338() { checkNotSubtype("int","{null f1}"); }
	@Test public void test_4339() { checkNotSubtype("int","{null f2}"); }
	@Test public void test_4340() { checkNotSubtype("int","{int f1}"); }
	@Test public void test_4341() { checkNotSubtype("int","{int f2}"); }
	@Test public void test_4350() { checkNotSubtype("int","{null f1,null f2}"); }
	@Test public void test_4351() { checkNotSubtype("int","{null f2,null f3}"); }
	@Test public void test_4352() { checkNotSubtype("int","{null f1,int f2}"); }
	@Test public void test_4353() { checkNotSubtype("int","{null f2,int f3}"); }
	@Test public void test_4356() { checkNotSubtype("int","{int f1,null f2}"); }
	@Test public void test_4357() { checkNotSubtype("int","{int f2,null f3}"); }
	@Test public void test_4358() { checkNotSubtype("int","{int f1,int f2}"); }
	@Test public void test_4359() { checkNotSubtype("int","{int f2,int f3}"); }
	@Test public void test_4372() { checkNotSubtype("int","{{null f1} f1}"); }
	@Test public void test_4373() { checkNotSubtype("int","{{null f2} f1}"); }
	@Test public void test_4374() { checkNotSubtype("int","{{null f1} f2}"); }
	@Test public void test_4375() { checkNotSubtype("int","{{null f2} f2}"); }
	@Test public void test_4376() { checkNotSubtype("int","{{null f1} f1,null f2}"); }
	@Test public void test_4377() { checkNotSubtype("int","{{null f2} f1,null f2}"); }
	@Test public void test_4378() { checkNotSubtype("int","{{null f1} f2,null f3}"); }
	@Test public void test_4379() { checkNotSubtype("int","{{null f2} f2,null f3}"); }
	@Test public void test_4380() { checkNotSubtype("int","{{int f1} f1}"); }
	@Test public void test_4381() { checkNotSubtype("int","{{int f2} f1}"); }
	@Test public void test_4382() { checkNotSubtype("int","{{int f1} f2}"); }
	@Test public void test_4383() { checkNotSubtype("int","{{int f2} f2}"); }
	@Test public void test_4384() { checkNotSubtype("int","{{int f1} f1,int f2}"); }
	@Test public void test_4385() { checkNotSubtype("int","{{int f2} f1,int f2}"); }
	@Test public void test_4386() { checkNotSubtype("int","{{int f1} f2,int f3}"); }
	@Test public void test_4387() { checkNotSubtype("int","{{int f2} f2,int f3}"); }
	@Test public void test_4389() { checkNotSubtype("int","null"); }
	@Test public void test_4390() { checkIsSubtype("int","int"); }
	@Test public void test_4395() { checkNotSubtype("int","null"); }
	@Test public void test_4397() { checkNotSubtype("int","null"); }
	@Test public void test_4398() { checkNotSubtype("int","null|int"); }
	@Test public void test_4399() { checkIsSubtype("int","int"); }
	@Test public void test_4401() { checkNotSubtype("int","int|null"); }
	@Test public void test_4402() { checkIsSubtype("int","int"); }
	@Test public void test_4405() { checkNotSubtype("int","{null f1}|null"); }
	@Test public void test_4406() { checkNotSubtype("int","{null f2}|null"); }
	@Test public void test_4407() { checkNotSubtype("int","{int f1}|int"); }
	@Test public void test_4408() { checkNotSubtype("int","{int f2}|int"); }
	@Test public void test_4714() { checkIsSubtype("null","null"); }
	@Test public void test_4715() { checkNotSubtype("null","int"); }
	@Test public void test_4718() { checkNotSubtype("null","{null f1}"); }
	@Test public void test_4719() { checkNotSubtype("null","{null f2}"); }
	@Test public void test_4720() { checkNotSubtype("null","{int f1}"); }
	@Test public void test_4721() { checkNotSubtype("null","{int f2}"); }
	@Test public void test_4730() { checkNotSubtype("null","{null f1,null f2}"); }
	@Test public void test_4731() { checkNotSubtype("null","{null f2,null f3}"); }
	@Test public void test_4732() { checkNotSubtype("null","{null f1,int f2}"); }
	@Test public void test_4733() { checkNotSubtype("null","{null f2,int f3}"); }
	@Test public void test_4736() { checkNotSubtype("null","{int f1,null f2}"); }
	@Test public void test_4737() { checkNotSubtype("null","{int f2,null f3}"); }
	@Test public void test_4738() { checkNotSubtype("null","{int f1,int f2}"); }
	@Test public void test_4739() { checkNotSubtype("null","{int f2,int f3}"); }
	@Test public void test_4752() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test public void test_4753() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test public void test_4754() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test public void test_4755() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test public void test_4756() { checkNotSubtype("null","{{null f1} f1,null f2}"); }
	@Test public void test_4757() { checkNotSubtype("null","{{null f2} f1,null f2}"); }
	@Test public void test_4758() { checkNotSubtype("null","{{null f1} f2,null f3}"); }
	@Test public void test_4759() { checkNotSubtype("null","{{null f2} f2,null f3}"); }
	@Test public void test_4760() { checkNotSubtype("null","{{int f1} f1}"); }
	@Test public void test_4761() { checkNotSubtype("null","{{int f2} f1}"); }
	@Test public void test_4762() { checkNotSubtype("null","{{int f1} f2}"); }
	@Test public void test_4763() { checkNotSubtype("null","{{int f2} f2}"); }
	@Test public void test_4764() { checkNotSubtype("null","{{int f1} f1,int f2}"); }
	@Test public void test_4765() { checkNotSubtype("null","{{int f2} f1,int f2}"); }
	@Test public void test_4766() { checkNotSubtype("null","{{int f1} f2,int f3}"); }
	@Test public void test_4767() { checkNotSubtype("null","{{int f2} f2,int f3}"); }
	@Test public void test_4769() { checkIsSubtype("null","null"); }
	@Test public void test_4770() { checkNotSubtype("null","int"); }
	@Test public void test_4775() { checkIsSubtype("null","null"); }
	@Test public void test_4777() { checkIsSubtype("null","null"); }
	@Test public void test_4778() { checkNotSubtype("null","null|int"); }
	@Test public void test_4779() { checkNotSubtype("null","int"); }
	@Test public void test_4781() { checkNotSubtype("null","int|null"); }
	@Test public void test_4782() { checkNotSubtype("null","int"); }
	@Test public void test_4785() { checkNotSubtype("null","{null f1}|null"); }
	@Test public void test_4786() { checkNotSubtype("null","{null f2}|null"); }
	@Test public void test_4787() { checkNotSubtype("null","{int f1}|int"); }
	@Test public void test_4788() { checkNotSubtype("null","{int f2}|int"); }
	@Test public void test_4866() { checkIsSubtype("null","null"); }
	@Test public void test_4867() { checkNotSubtype("null","int"); }
	@Test public void test_4870() { checkNotSubtype("null","{null f1}"); }
	@Test public void test_4871() { checkNotSubtype("null","{null f2}"); }
	@Test public void test_4872() { checkNotSubtype("null","{int f1}"); }
	@Test public void test_4873() { checkNotSubtype("null","{int f2}"); }
	@Test public void test_4882() { checkNotSubtype("null","{null f1,null f2}"); }
	@Test public void test_4883() { checkNotSubtype("null","{null f2,null f3}"); }
	@Test public void test_4884() { checkNotSubtype("null","{null f1,int f2}"); }
	@Test public void test_4885() { checkNotSubtype("null","{null f2,int f3}"); }
	@Test public void test_4888() { checkNotSubtype("null","{int f1,null f2}"); }
	@Test public void test_4889() { checkNotSubtype("null","{int f2,null f3}"); }
	@Test public void test_4890() { checkNotSubtype("null","{int f1,int f2}"); }
	@Test public void test_4891() { checkNotSubtype("null","{int f2,int f3}"); }
	@Test public void test_4904() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test public void test_4905() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test public void test_4906() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test public void test_4907() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test public void test_4908() { checkNotSubtype("null","{{null f1} f1,null f2}"); }
	@Test public void test_4909() { checkNotSubtype("null","{{null f2} f1,null f2}"); }
	@Test public void test_4910() { checkNotSubtype("null","{{null f1} f2,null f3}"); }
	@Test public void test_4911() { checkNotSubtype("null","{{null f2} f2,null f3}"); }
	@Test public void test_4912() { checkNotSubtype("null","{{int f1} f1}"); }
	@Test public void test_4913() { checkNotSubtype("null","{{int f2} f1}"); }
	@Test public void test_4914() { checkNotSubtype("null","{{int f1} f2}"); }
	@Test public void test_4915() { checkNotSubtype("null","{{int f2} f2}"); }
	@Test public void test_4916() { checkNotSubtype("null","{{int f1} f1,int f2}"); }
	@Test public void test_4917() { checkNotSubtype("null","{{int f2} f1,int f2}"); }
	@Test public void test_4918() { checkNotSubtype("null","{{int f1} f2,int f3}"); }
	@Test public void test_4919() { checkNotSubtype("null","{{int f2} f2,int f3}"); }
	@Test public void test_4921() { checkIsSubtype("null","null"); }
	@Test public void test_4922() { checkNotSubtype("null","int"); }
	@Test public void test_4927() { checkIsSubtype("null","null"); }
	@Test public void test_4929() { checkIsSubtype("null","null"); }
	@Test public void test_4930() { checkNotSubtype("null","null|int"); }
	@Test public void test_4931() { checkNotSubtype("null","int"); }
	@Test public void test_4933() { checkNotSubtype("null","int|null"); }
	@Test public void test_4934() { checkNotSubtype("null","int"); }
	@Test public void test_4937() { checkNotSubtype("null","{null f1}|null"); }
	@Test public void test_4938() { checkNotSubtype("null","{null f2}|null"); }
	@Test public void test_4939() { checkNotSubtype("null","{int f1}|int"); }
	@Test public void test_4940() { checkNotSubtype("null","{int f2}|int"); }
	@Test public void test_4942() { checkIsSubtype("null|int","null"); }
	@Test public void test_4943() { checkIsSubtype("null|int","int"); }
	@Test public void test_4946() { checkNotSubtype("null|int","{null f1}"); }
	@Test public void test_4947() { checkNotSubtype("null|int","{null f2}"); }
	@Test public void test_4948() { checkNotSubtype("null|int","{int f1}"); }
	@Test public void test_4949() { checkNotSubtype("null|int","{int f2}"); }
	@Test public void test_4958() { checkNotSubtype("null|int","{null f1,null f2}"); }
	@Test public void test_4959() { checkNotSubtype("null|int","{null f2,null f3}"); }
	@Test public void test_4960() { checkNotSubtype("null|int","{null f1,int f2}"); }
	@Test public void test_4961() { checkNotSubtype("null|int","{null f2,int f3}"); }
	@Test public void test_4964() { checkNotSubtype("null|int","{int f1,null f2}"); }
	@Test public void test_4965() { checkNotSubtype("null|int","{int f2,null f3}"); }
	@Test public void test_4966() { checkNotSubtype("null|int","{int f1,int f2}"); }
	@Test public void test_4967() { checkNotSubtype("null|int","{int f2,int f3}"); }
	@Test public void test_4980() { checkNotSubtype("null|int","{{null f1} f1}"); }
	@Test public void test_4981() { checkNotSubtype("null|int","{{null f2} f1}"); }
	@Test public void test_4982() { checkNotSubtype("null|int","{{null f1} f2}"); }
	@Test public void test_4983() { checkNotSubtype("null|int","{{null f2} f2}"); }
	@Test public void test_4984() { checkNotSubtype("null|int","{{null f1} f1,null f2}"); }
	@Test public void test_4985() { checkNotSubtype("null|int","{{null f2} f1,null f2}"); }
	@Test public void test_4986() { checkNotSubtype("null|int","{{null f1} f2,null f3}"); }
	@Test public void test_4987() { checkNotSubtype("null|int","{{null f2} f2,null f3}"); }
	@Test public void test_4988() { checkNotSubtype("null|int","{{int f1} f1}"); }
	@Test public void test_4989() { checkNotSubtype("null|int","{{int f2} f1}"); }
	@Test public void test_4990() { checkNotSubtype("null|int","{{int f1} f2}"); }
	@Test public void test_4991() { checkNotSubtype("null|int","{{int f2} f2}"); }
	@Test public void test_4992() { checkNotSubtype("null|int","{{int f1} f1,int f2}"); }
	@Test public void test_4993() { checkNotSubtype("null|int","{{int f2} f1,int f2}"); }
	@Test public void test_4994() { checkNotSubtype("null|int","{{int f1} f2,int f3}"); }
	@Test public void test_4995() { checkNotSubtype("null|int","{{int f2} f2,int f3}"); }
	@Test public void test_4997() { checkIsSubtype("null|int","null"); }
	@Test public void test_4998() { checkIsSubtype("null|int","int"); }
	@Test public void test_5003() { checkIsSubtype("null|int","null"); }
	@Test public void test_5005() { checkIsSubtype("null|int","null"); }
	@Test public void test_5006() { checkIsSubtype("null|int","null|int"); }
	@Test public void test_5007() { checkIsSubtype("null|int","int"); }
	@Test public void test_5009() { checkIsSubtype("null|int","int|null"); }
	@Test public void test_5010() { checkIsSubtype("null|int","int"); }
	@Test public void test_5013() { checkNotSubtype("null|int","{null f1}|null"); }
	@Test public void test_5014() { checkNotSubtype("null|int","{null f2}|null"); }
	@Test public void test_5015() { checkNotSubtype("null|int","{int f1}|int"); }
	@Test public void test_5016() { checkNotSubtype("null|int","{int f2}|int"); }
	@Test public void test_5018() { checkNotSubtype("int","null"); }
	@Test public void test_5019() { checkIsSubtype("int","int"); }
	@Test public void test_5022() { checkNotSubtype("int","{null f1}"); }
	@Test public void test_5023() { checkNotSubtype("int","{null f2}"); }
	@Test public void test_5024() { checkNotSubtype("int","{int f1}"); }
	@Test public void test_5025() { checkNotSubtype("int","{int f2}"); }
	@Test public void test_5034() { checkNotSubtype("int","{null f1,null f2}"); }
	@Test public void test_5035() { checkNotSubtype("int","{null f2,null f3}"); }
	@Test public void test_5036() { checkNotSubtype("int","{null f1,int f2}"); }
	@Test public void test_5037() { checkNotSubtype("int","{null f2,int f3}"); }
	@Test public void test_5040() { checkNotSubtype("int","{int f1,null f2}"); }
	@Test public void test_5041() { checkNotSubtype("int","{int f2,null f3}"); }
	@Test public void test_5042() { checkNotSubtype("int","{int f1,int f2}"); }
	@Test public void test_5043() { checkNotSubtype("int","{int f2,int f3}"); }
	@Test public void test_5056() { checkNotSubtype("int","{{null f1} f1}"); }
	@Test public void test_5057() { checkNotSubtype("int","{{null f2} f1}"); }
	@Test public void test_5058() { checkNotSubtype("int","{{null f1} f2}"); }
	@Test public void test_5059() { checkNotSubtype("int","{{null f2} f2}"); }
	@Test public void test_5060() { checkNotSubtype("int","{{null f1} f1,null f2}"); }
	@Test public void test_5061() { checkNotSubtype("int","{{null f2} f1,null f2}"); }
	@Test public void test_5062() { checkNotSubtype("int","{{null f1} f2,null f3}"); }
	@Test public void test_5063() { checkNotSubtype("int","{{null f2} f2,null f3}"); }
	@Test public void test_5064() { checkNotSubtype("int","{{int f1} f1}"); }
	@Test public void test_5065() { checkNotSubtype("int","{{int f2} f1}"); }
	@Test public void test_5066() { checkNotSubtype("int","{{int f1} f2}"); }
	@Test public void test_5067() { checkNotSubtype("int","{{int f2} f2}"); }
	@Test public void test_5068() { checkNotSubtype("int","{{int f1} f1,int f2}"); }
	@Test public void test_5069() { checkNotSubtype("int","{{int f2} f1,int f2}"); }
	@Test public void test_5070() { checkNotSubtype("int","{{int f1} f2,int f3}"); }
	@Test public void test_5071() { checkNotSubtype("int","{{int f2} f2,int f3}"); }
	@Test public void test_5073() { checkNotSubtype("int","null"); }
	@Test public void test_5074() { checkIsSubtype("int","int"); }
	@Test public void test_5079() { checkNotSubtype("int","null"); }
	@Test public void test_5081() { checkNotSubtype("int","null"); }
	@Test public void test_5082() { checkNotSubtype("int","null|int"); }
	@Test public void test_5083() { checkIsSubtype("int","int"); }
	@Test public void test_5085() { checkNotSubtype("int","int|null"); }
	@Test public void test_5086() { checkIsSubtype("int","int"); }
	@Test public void test_5089() { checkNotSubtype("int","{null f1}|null"); }
	@Test public void test_5090() { checkNotSubtype("int","{null f2}|null"); }
	@Test public void test_5091() { checkNotSubtype("int","{int f1}|int"); }
	@Test public void test_5092() { checkNotSubtype("int","{int f2}|int"); }
	@Test public void test_5170() { checkIsSubtype("int|null","null"); }
	@Test public void test_5171() { checkIsSubtype("int|null","int"); }
	@Test public void test_5174() { checkNotSubtype("int|null","{null f1}"); }
	@Test public void test_5175() { checkNotSubtype("int|null","{null f2}"); }
	@Test public void test_5176() { checkNotSubtype("int|null","{int f1}"); }
	@Test public void test_5177() { checkNotSubtype("int|null","{int f2}"); }
	@Test public void test_5186() { checkNotSubtype("int|null","{null f1,null f2}"); }
	@Test public void test_5187() { checkNotSubtype("int|null","{null f2,null f3}"); }
	@Test public void test_5188() { checkNotSubtype("int|null","{null f1,int f2}"); }
	@Test public void test_5189() { checkNotSubtype("int|null","{null f2,int f3}"); }
	@Test public void test_5192() { checkNotSubtype("int|null","{int f1,null f2}"); }
	@Test public void test_5193() { checkNotSubtype("int|null","{int f2,null f3}"); }
	@Test public void test_5194() { checkNotSubtype("int|null","{int f1,int f2}"); }
	@Test public void test_5195() { checkNotSubtype("int|null","{int f2,int f3}"); }
	@Test public void test_5208() { checkNotSubtype("int|null","{{null f1} f1}"); }
	@Test public void test_5209() { checkNotSubtype("int|null","{{null f2} f1}"); }
	@Test public void test_5210() { checkNotSubtype("int|null","{{null f1} f2}"); }
	@Test public void test_5211() { checkNotSubtype("int|null","{{null f2} f2}"); }
	@Test public void test_5212() { checkNotSubtype("int|null","{{null f1} f1,null f2}"); }
	@Test public void test_5213() { checkNotSubtype("int|null","{{null f2} f1,null f2}"); }
	@Test public void test_5214() { checkNotSubtype("int|null","{{null f1} f2,null f3}"); }
	@Test public void test_5215() { checkNotSubtype("int|null","{{null f2} f2,null f3}"); }
	@Test public void test_5216() { checkNotSubtype("int|null","{{int f1} f1}"); }
	@Test public void test_5217() { checkNotSubtype("int|null","{{int f2} f1}"); }
	@Test public void test_5218() { checkNotSubtype("int|null","{{int f1} f2}"); }
	@Test public void test_5219() { checkNotSubtype("int|null","{{int f2} f2}"); }
	@Test public void test_5220() { checkNotSubtype("int|null","{{int f1} f1,int f2}"); }
	@Test public void test_5221() { checkNotSubtype("int|null","{{int f2} f1,int f2}"); }
	@Test public void test_5222() { checkNotSubtype("int|null","{{int f1} f2,int f3}"); }
	@Test public void test_5223() { checkNotSubtype("int|null","{{int f2} f2,int f3}"); }
	@Test public void test_5225() { checkIsSubtype("int|null","null"); }
	@Test public void test_5226() { checkIsSubtype("int|null","int"); }
	@Test public void test_5231() { checkIsSubtype("int|null","null"); }
	@Test public void test_5233() { checkIsSubtype("int|null","null"); }
	@Test public void test_5234() { checkIsSubtype("int|null","null|int"); }
	@Test public void test_5235() { checkIsSubtype("int|null","int"); }
	@Test public void test_5237() { checkIsSubtype("int|null","int|null"); }
	@Test public void test_5238() { checkIsSubtype("int|null","int"); }
	@Test public void test_5241() { checkNotSubtype("int|null","{null f1}|null"); }
	@Test public void test_5242() { checkNotSubtype("int|null","{null f2}|null"); }
	@Test public void test_5243() { checkNotSubtype("int|null","{int f1}|int"); }
	@Test public void test_5244() { checkNotSubtype("int|null","{int f2}|int"); }
	@Test public void test_5246() { checkNotSubtype("int","null"); }
	@Test public void test_5247() { checkIsSubtype("int","int"); }
	@Test public void test_5250() { checkNotSubtype("int","{null f1}"); }
	@Test public void test_5251() { checkNotSubtype("int","{null f2}"); }
	@Test public void test_5252() { checkNotSubtype("int","{int f1}"); }
	@Test public void test_5253() { checkNotSubtype("int","{int f2}"); }
	@Test public void test_5262() { checkNotSubtype("int","{null f1,null f2}"); }
	@Test public void test_5263() { checkNotSubtype("int","{null f2,null f3}"); }
	@Test public void test_5264() { checkNotSubtype("int","{null f1,int f2}"); }
	@Test public void test_5265() { checkNotSubtype("int","{null f2,int f3}"); }
	@Test public void test_5268() { checkNotSubtype("int","{int f1,null f2}"); }
	@Test public void test_5269() { checkNotSubtype("int","{int f2,null f3}"); }
	@Test public void test_5270() { checkNotSubtype("int","{int f1,int f2}"); }
	@Test public void test_5271() { checkNotSubtype("int","{int f2,int f3}"); }
	@Test public void test_5284() { checkNotSubtype("int","{{null f1} f1}"); }
	@Test public void test_5285() { checkNotSubtype("int","{{null f2} f1}"); }
	@Test public void test_5286() { checkNotSubtype("int","{{null f1} f2}"); }
	@Test public void test_5287() { checkNotSubtype("int","{{null f2} f2}"); }
	@Test public void test_5288() { checkNotSubtype("int","{{null f1} f1,null f2}"); }
	@Test public void test_5289() { checkNotSubtype("int","{{null f2} f1,null f2}"); }
	@Test public void test_5290() { checkNotSubtype("int","{{null f1} f2,null f3}"); }
	@Test public void test_5291() { checkNotSubtype("int","{{null f2} f2,null f3}"); }
	@Test public void test_5292() { checkNotSubtype("int","{{int f1} f1}"); }
	@Test public void test_5293() { checkNotSubtype("int","{{int f2} f1}"); }
	@Test public void test_5294() { checkNotSubtype("int","{{int f1} f2}"); }
	@Test public void test_5295() { checkNotSubtype("int","{{int f2} f2}"); }
	@Test public void test_5296() { checkNotSubtype("int","{{int f1} f1,int f2}"); }
	@Test public void test_5297() { checkNotSubtype("int","{{int f2} f1,int f2}"); }
	@Test public void test_5298() { checkNotSubtype("int","{{int f1} f2,int f3}"); }
	@Test public void test_5299() { checkNotSubtype("int","{{int f2} f2,int f3}"); }
	@Test public void test_5301() { checkNotSubtype("int","null"); }
	@Test public void test_5302() { checkIsSubtype("int","int"); }
	@Test public void test_5307() { checkNotSubtype("int","null"); }
	@Test public void test_5309() { checkNotSubtype("int","null"); }
	@Test public void test_5310() { checkNotSubtype("int","null|int"); }
	@Test public void test_5311() { checkIsSubtype("int","int"); }
	@Test public void test_5313() { checkNotSubtype("int","int|null"); }
	@Test public void test_5314() { checkIsSubtype("int","int"); }
	@Test public void test_5317() { checkNotSubtype("int","{null f1}|null"); }
	@Test public void test_5318() { checkNotSubtype("int","{null f2}|null"); }
	@Test public void test_5319() { checkNotSubtype("int","{int f1}|int"); }
	@Test public void test_5320() { checkNotSubtype("int","{int f2}|int"); }
	@Test public void test_5474() { checkIsSubtype("{null f1}|null","null"); }
	@Test public void test_5475() { checkNotSubtype("{null f1}|null","int"); }
	@Test public void test_5478() { checkIsSubtype("{null f1}|null","{null f1}"); }
	@Test public void test_5479() { checkNotSubtype("{null f1}|null","{null f2}"); }
	@Test public void test_5480() { checkNotSubtype("{null f1}|null","{int f1}"); }
	@Test public void test_5481() { checkNotSubtype("{null f1}|null","{int f2}"); }
	@Test public void test_5490() { checkNotSubtype("{null f1}|null","{null f1,null f2}"); }
	@Test public void test_5491() { checkNotSubtype("{null f1}|null","{null f2,null f3}"); }
	@Test public void test_5492() { checkNotSubtype("{null f1}|null","{null f1,int f2}"); }
	@Test public void test_5493() { checkNotSubtype("{null f1}|null","{null f2,int f3}"); }
	@Test public void test_5496() { checkNotSubtype("{null f1}|null","{int f1,null f2}"); }
	@Test public void test_5497() { checkNotSubtype("{null f1}|null","{int f2,null f3}"); }
	@Test public void test_5498() { checkNotSubtype("{null f1}|null","{int f1,int f2}"); }
	@Test public void test_5499() { checkNotSubtype("{null f1}|null","{int f2,int f3}"); }
	@Test public void test_5512() { checkNotSubtype("{null f1}|null","{{null f1} f1}"); }
	@Test public void test_5513() { checkNotSubtype("{null f1}|null","{{null f2} f1}"); }
	@Test public void test_5514() { checkNotSubtype("{null f1}|null","{{null f1} f2}"); }
	@Test public void test_5515() { checkNotSubtype("{null f1}|null","{{null f2} f2}"); }
	@Test public void test_5516() { checkNotSubtype("{null f1}|null","{{null f1} f1,null f2}"); }
	@Test public void test_5517() { checkNotSubtype("{null f1}|null","{{null f2} f1,null f2}"); }
	@Test public void test_5518() { checkNotSubtype("{null f1}|null","{{null f1} f2,null f3}"); }
	@Test public void test_5519() { checkNotSubtype("{null f1}|null","{{null f2} f2,null f3}"); }
	@Test public void test_5520() { checkNotSubtype("{null f1}|null","{{int f1} f1}"); }
	@Test public void test_5521() { checkNotSubtype("{null f1}|null","{{int f2} f1}"); }
	@Test public void test_5522() { checkNotSubtype("{null f1}|null","{{int f1} f2}"); }
	@Test public void test_5523() { checkNotSubtype("{null f1}|null","{{int f2} f2}"); }
	@Test public void test_5524() { checkNotSubtype("{null f1}|null","{{int f1} f1,int f2}"); }
	@Test public void test_5525() { checkNotSubtype("{null f1}|null","{{int f2} f1,int f2}"); }
	@Test public void test_5526() { checkNotSubtype("{null f1}|null","{{int f1} f2,int f3}"); }
	@Test public void test_5527() { checkNotSubtype("{null f1}|null","{{int f2} f2,int f3}"); }
	@Test public void test_5529() { checkIsSubtype("{null f1}|null","null"); }
	@Test public void test_5530() { checkNotSubtype("{null f1}|null","int"); }
	@Test public void test_5535() { checkIsSubtype("{null f1}|null","null"); }
	@Test public void test_5537() { checkIsSubtype("{null f1}|null","null"); }
	@Test public void test_5538() { checkNotSubtype("{null f1}|null","null|int"); }
	@Test public void test_5539() { checkNotSubtype("{null f1}|null","int"); }
	@Test public void test_5541() { checkNotSubtype("{null f1}|null","int|null"); }
	@Test public void test_5542() { checkNotSubtype("{null f1}|null","int"); }
	@Test public void test_5545() { checkIsSubtype("{null f1}|null","{null f1}|null"); }
	@Test public void test_5546() { checkNotSubtype("{null f1}|null","{null f2}|null"); }
	@Test public void test_5547() { checkNotSubtype("{null f1}|null","{int f1}|int"); }
	@Test public void test_5548() { checkNotSubtype("{null f1}|null","{int f2}|int"); }
	@Test public void test_5550() { checkIsSubtype("{null f2}|null","null"); }
	@Test public void test_5551() { checkNotSubtype("{null f2}|null","int"); }
	@Test public void test_5554() { checkNotSubtype("{null f2}|null","{null f1}"); }
	@Test public void test_5555() { checkIsSubtype("{null f2}|null","{null f2}"); }
	@Test public void test_5556() { checkNotSubtype("{null f2}|null","{int f1}"); }
	@Test public void test_5557() { checkNotSubtype("{null f2}|null","{int f2}"); }
	@Test public void test_5566() { checkNotSubtype("{null f2}|null","{null f1,null f2}"); }
	@Test public void test_5567() { checkNotSubtype("{null f2}|null","{null f2,null f3}"); }
	@Test public void test_5568() { checkNotSubtype("{null f2}|null","{null f1,int f2}"); }
	@Test public void test_5569() { checkNotSubtype("{null f2}|null","{null f2,int f3}"); }
	@Test public void test_5572() { checkNotSubtype("{null f2}|null","{int f1,null f2}"); }
	@Test public void test_5573() { checkNotSubtype("{null f2}|null","{int f2,null f3}"); }
	@Test public void test_5574() { checkNotSubtype("{null f2}|null","{int f1,int f2}"); }
	@Test public void test_5575() { checkNotSubtype("{null f2}|null","{int f2,int f3}"); }
	@Test public void test_5588() { checkNotSubtype("{null f2}|null","{{null f1} f1}"); }
	@Test public void test_5589() { checkNotSubtype("{null f2}|null","{{null f2} f1}"); }
	@Test public void test_5590() { checkNotSubtype("{null f2}|null","{{null f1} f2}"); }
	@Test public void test_5591() { checkNotSubtype("{null f2}|null","{{null f2} f2}"); }
	@Test public void test_5592() { checkNotSubtype("{null f2}|null","{{null f1} f1,null f2}"); }
	@Test public void test_5593() { checkNotSubtype("{null f2}|null","{{null f2} f1,null f2}"); }
	@Test public void test_5594() { checkNotSubtype("{null f2}|null","{{null f1} f2,null f3}"); }
	@Test public void test_5595() { checkNotSubtype("{null f2}|null","{{null f2} f2,null f3}"); }
	@Test public void test_5596() { checkNotSubtype("{null f2}|null","{{int f1} f1}"); }
	@Test public void test_5597() { checkNotSubtype("{null f2}|null","{{int f2} f1}"); }
	@Test public void test_5598() { checkNotSubtype("{null f2}|null","{{int f1} f2}"); }
	@Test public void test_5599() { checkNotSubtype("{null f2}|null","{{int f2} f2}"); }
	@Test public void test_5600() { checkNotSubtype("{null f2}|null","{{int f1} f1,int f2}"); }
	@Test public void test_5601() { checkNotSubtype("{null f2}|null","{{int f2} f1,int f2}"); }
	@Test public void test_5602() { checkNotSubtype("{null f2}|null","{{int f1} f2,int f3}"); }
	@Test public void test_5603() { checkNotSubtype("{null f2}|null","{{int f2} f2,int f3}"); }
	@Test public void test_5605() { checkIsSubtype("{null f2}|null","null"); }
	@Test public void test_5606() { checkNotSubtype("{null f2}|null","int"); }
	@Test public void test_5611() { checkIsSubtype("{null f2}|null","null"); }
	@Test public void test_5613() { checkIsSubtype("{null f2}|null","null"); }
	@Test public void test_5614() { checkNotSubtype("{null f2}|null","null|int"); }
	@Test public void test_5615() { checkNotSubtype("{null f2}|null","int"); }
	@Test public void test_5617() { checkNotSubtype("{null f2}|null","int|null"); }
	@Test public void test_5618() { checkNotSubtype("{null f2}|null","int"); }
	@Test public void test_5621() { checkNotSubtype("{null f2}|null","{null f1}|null"); }
	@Test public void test_5622() { checkIsSubtype("{null f2}|null","{null f2}|null"); }
	@Test public void test_5623() { checkNotSubtype("{null f2}|null","{int f1}|int"); }
	@Test public void test_5624() { checkNotSubtype("{null f2}|null","{int f2}|int"); }
	@Test public void test_5626() { checkNotSubtype("{int f1}|int","null"); }
	@Test public void test_5627() { checkIsSubtype("{int f1}|int","int"); }
	@Test public void test_5630() { checkNotSubtype("{int f1}|int","{null f1}"); }
	@Test public void test_5631() { checkNotSubtype("{int f1}|int","{null f2}"); }
	@Test public void test_5632() { checkIsSubtype("{int f1}|int","{int f1}"); }
	@Test public void test_5633() { checkNotSubtype("{int f1}|int","{int f2}"); }
	@Test public void test_5642() { checkNotSubtype("{int f1}|int","{null f1,null f2}"); }
	@Test public void test_5643() { checkNotSubtype("{int f1}|int","{null f2,null f3}"); }
	@Test public void test_5644() { checkNotSubtype("{int f1}|int","{null f1,int f2}"); }
	@Test public void test_5645() { checkNotSubtype("{int f1}|int","{null f2,int f3}"); }
	@Test public void test_5648() { checkNotSubtype("{int f1}|int","{int f1,null f2}"); }
	@Test public void test_5649() { checkNotSubtype("{int f1}|int","{int f2,null f3}"); }
	@Test public void test_5650() { checkNotSubtype("{int f1}|int","{int f1,int f2}"); }
	@Test public void test_5651() { checkNotSubtype("{int f1}|int","{int f2,int f3}"); }
	@Test public void test_5664() { checkNotSubtype("{int f1}|int","{{null f1} f1}"); }
	@Test public void test_5665() { checkNotSubtype("{int f1}|int","{{null f2} f1}"); }
	@Test public void test_5666() { checkNotSubtype("{int f1}|int","{{null f1} f2}"); }
	@Test public void test_5667() { checkNotSubtype("{int f1}|int","{{null f2} f2}"); }
	@Test public void test_5668() { checkNotSubtype("{int f1}|int","{{null f1} f1,null f2}"); }
	@Test public void test_5669() { checkNotSubtype("{int f1}|int","{{null f2} f1,null f2}"); }
	@Test public void test_5670() { checkNotSubtype("{int f1}|int","{{null f1} f2,null f3}"); }
	@Test public void test_5671() { checkNotSubtype("{int f1}|int","{{null f2} f2,null f3}"); }
	@Test public void test_5672() { checkNotSubtype("{int f1}|int","{{int f1} f1}"); }
	@Test public void test_5673() { checkNotSubtype("{int f1}|int","{{int f2} f1}"); }
	@Test public void test_5674() { checkNotSubtype("{int f1}|int","{{int f1} f2}"); }
	@Test public void test_5675() { checkNotSubtype("{int f1}|int","{{int f2} f2}"); }
	@Test public void test_5676() { checkNotSubtype("{int f1}|int","{{int f1} f1,int f2}"); }
	@Test public void test_5677() { checkNotSubtype("{int f1}|int","{{int f2} f1,int f2}"); }
	@Test public void test_5678() { checkNotSubtype("{int f1}|int","{{int f1} f2,int f3}"); }
	@Test public void test_5679() { checkNotSubtype("{int f1}|int","{{int f2} f2,int f3}"); }
	@Test public void test_5681() { checkNotSubtype("{int f1}|int","null"); }
	@Test public void test_5682() { checkIsSubtype("{int f1}|int","int"); }
	@Test public void test_5687() { checkNotSubtype("{int f1}|int","null"); }
	@Test public void test_5689() { checkNotSubtype("{int f1}|int","null"); }
	@Test public void test_5690() { checkNotSubtype("{int f1}|int","null|int"); }
	@Test public void test_5691() { checkIsSubtype("{int f1}|int","int"); }
	@Test public void test_5693() { checkNotSubtype("{int f1}|int","int|null"); }
	@Test public void test_5694() { checkIsSubtype("{int f1}|int","int"); }
	@Test public void test_5697() { checkNotSubtype("{int f1}|int","{null f1}|null"); }
	@Test public void test_5698() { checkNotSubtype("{int f1}|int","{null f2}|null"); }
	@Test public void test_5699() { checkIsSubtype("{int f1}|int","{int f1}|int"); }
	@Test public void test_5700() { checkNotSubtype("{int f1}|int","{int f2}|int"); }
	@Test public void test_5702() { checkNotSubtype("{int f2}|int","null"); }
	@Test public void test_5703() { checkIsSubtype("{int f2}|int","int"); }
	@Test public void test_5706() { checkNotSubtype("{int f2}|int","{null f1}"); }
	@Test public void test_5707() { checkNotSubtype("{int f2}|int","{null f2}"); }
	@Test public void test_5708() { checkNotSubtype("{int f2}|int","{int f1}"); }
	@Test public void test_5709() { checkIsSubtype("{int f2}|int","{int f2}"); }
	@Test public void test_5718() { checkNotSubtype("{int f2}|int","{null f1,null f2}"); }
	@Test public void test_5719() { checkNotSubtype("{int f2}|int","{null f2,null f3}"); }
	@Test public void test_5720() { checkNotSubtype("{int f2}|int","{null f1,int f2}"); }
	@Test public void test_5721() { checkNotSubtype("{int f2}|int","{null f2,int f3}"); }
	@Test public void test_5724() { checkNotSubtype("{int f2}|int","{int f1,null f2}"); }
	@Test public void test_5725() { checkNotSubtype("{int f2}|int","{int f2,null f3}"); }
	@Test public void test_5726() { checkNotSubtype("{int f2}|int","{int f1,int f2}"); }
	@Test public void test_5727() { checkNotSubtype("{int f2}|int","{int f2,int f3}"); }
	@Test public void test_5740() { checkNotSubtype("{int f2}|int","{{null f1} f1}"); }
	@Test public void test_5741() { checkNotSubtype("{int f2}|int","{{null f2} f1}"); }
	@Test public void test_5742() { checkNotSubtype("{int f2}|int","{{null f1} f2}"); }
	@Test public void test_5743() { checkNotSubtype("{int f2}|int","{{null f2} f2}"); }
	@Test public void test_5744() { checkNotSubtype("{int f2}|int","{{null f1} f1,null f2}"); }
	@Test public void test_5745() { checkNotSubtype("{int f2}|int","{{null f2} f1,null f2}"); }
	@Test public void test_5746() { checkNotSubtype("{int f2}|int","{{null f1} f2,null f3}"); }
	@Test public void test_5747() { checkNotSubtype("{int f2}|int","{{null f2} f2,null f3}"); }
	@Test public void test_5748() { checkNotSubtype("{int f2}|int","{{int f1} f1}"); }
	@Test public void test_5749() { checkNotSubtype("{int f2}|int","{{int f2} f1}"); }
	@Test public void test_5750() { checkNotSubtype("{int f2}|int","{{int f1} f2}"); }
	@Test public void test_5751() { checkNotSubtype("{int f2}|int","{{int f2} f2}"); }
	@Test public void test_5752() { checkNotSubtype("{int f2}|int","{{int f1} f1,int f2}"); }
	@Test public void test_5753() { checkNotSubtype("{int f2}|int","{{int f2} f1,int f2}"); }
	@Test public void test_5754() { checkNotSubtype("{int f2}|int","{{int f1} f2,int f3}"); }
	@Test public void test_5755() { checkNotSubtype("{int f2}|int","{{int f2} f2,int f3}"); }
	@Test public void test_5757() { checkNotSubtype("{int f2}|int","null"); }
	@Test public void test_5758() { checkIsSubtype("{int f2}|int","int"); }
	@Test public void test_5763() { checkNotSubtype("{int f2}|int","null"); }
	@Test public void test_5765() { checkNotSubtype("{int f2}|int","null"); }
	@Test public void test_5766() { checkNotSubtype("{int f2}|int","null|int"); }
	@Test public void test_5767() { checkIsSubtype("{int f2}|int","int"); }
	@Test public void test_5769() { checkNotSubtype("{int f2}|int","int|null"); }
	@Test public void test_5770() { checkIsSubtype("{int f2}|int","int"); }
	@Test public void test_5773() { checkNotSubtype("{int f2}|int","{null f1}|null"); }
	@Test public void test_5774() { checkNotSubtype("{int f2}|int","{null f2}|null"); }
	@Test public void test_5775() { checkNotSubtype("{int f2}|int","{int f1}|int"); }
	@Test public void test_5776() { checkIsSubtype("{int f2}|int","{int f2}|int"); }

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
