// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http:www.apache.org/licenses/LICENSE-2.0
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
import static org.junit.Assert.*;

import wybs.lang.NameResolver;
import static wyc.lang.WhileyFile.Type;

import wyc.util.TestUtils;
import wyil.type.subtyping.RelaxedTypeEmptinessTest;
import wyil.type.subtyping.SubtypeOperator;
import wyil.type.subtyping.StrictTypeEmptinessTest;

public class ArraySubtypeTest {
	@Test public void test_52() { checkIsSubtype("null","null"); }
	@Test public void test_53() { checkNotSubtype("null","int"); }
	@Test public void test_60() { checkNotSubtype("null","null[]"); }
	@Test public void test_61() { checkNotSubtype("null","int[]"); }
	@Test public void test_76() { checkNotSubtype("null","null[][]"); }
	@Test public void test_77() { checkNotSubtype("null","int[][]"); }
	@Test public void test_79() { checkIsSubtype("null","null"); }
	@Test public void test_80() { checkNotSubtype("null","int"); }
	@Test public void test_85() { checkIsSubtype("null","null"); }
	@Test public void test_87() { checkIsSubtype("null","null"); }
	@Test public void test_88() { checkNotSubtype("null","null|int"); }
	@Test public void test_89() { checkNotSubtype("null","int"); }
	@Test public void test_91() { checkNotSubtype("null","int|null"); }
	@Test public void test_92() { checkNotSubtype("null","int"); }
	@Test public void test_99() { checkNotSubtype("null","null[]|null"); }
	@Test public void test_100() { checkNotSubtype("null","int[]|int"); }
	@Test public void test_102() { checkNotSubtype("int","null"); }
	@Test public void test_103() { checkIsSubtype("int","int"); }
	@Test public void test_110() { checkNotSubtype("int","null[]"); }
	@Test public void test_111() { checkNotSubtype("int","int[]"); }
	@Test public void test_126() { checkNotSubtype("int","null[][]"); }
	@Test public void test_127() { checkNotSubtype("int","int[][]"); }
	@Test public void test_129() { checkNotSubtype("int","null"); }
	@Test public void test_130() { checkIsSubtype("int","int"); }
	@Test public void test_135() { checkNotSubtype("int","null"); }
	@Test public void test_137() { checkNotSubtype("int","null"); }
	@Test public void test_138() { checkNotSubtype("int","null|int"); }
	@Test public void test_139() { checkIsSubtype("int","int"); }
	@Test public void test_141() { checkNotSubtype("int","int|null"); }
	@Test public void test_142() { checkIsSubtype("int","int"); }
	@Test public void test_149() { checkNotSubtype("int","null[]|null"); }
	@Test public void test_150() { checkNotSubtype("int","int[]|int"); }
	@Test public void test_452() { checkNotSubtype("null[]","null"); }
	@Test public void test_453() { checkNotSubtype("null[]","int"); }
	@Test public void test_460() { checkIsSubtype("null[]","null[]"); }
	@Test public void test_461() { checkNotSubtype("null[]","int[]"); }
	@Test public void test_476() { checkNotSubtype("null[]","null[][]"); }
	@Test public void test_477() { checkNotSubtype("null[]","int[][]"); }
	@Test public void test_479() { checkNotSubtype("null[]","null"); }
	@Test public void test_480() { checkNotSubtype("null[]","int"); }
	@Test public void test_485() { checkNotSubtype("null[]","null"); }
	@Test public void test_487() { checkNotSubtype("null[]","null"); }
	@Test public void test_488() { checkNotSubtype("null[]","null|int"); }
	@Test public void test_489() { checkNotSubtype("null[]","int"); }
	@Test public void test_491() { checkNotSubtype("null[]","int|null"); }
	@Test public void test_492() { checkNotSubtype("null[]","int"); }
	@Test public void test_499() { checkNotSubtype("null[]","null[]|null"); }
	@Test public void test_500() { checkNotSubtype("null[]","int[]|int"); }
	@Test public void test_502() { checkNotSubtype("int[]","null"); }
	@Test public void test_503() { checkNotSubtype("int[]","int"); }
	@Test public void test_510() { checkNotSubtype("int[]","null[]"); }
	@Test public void test_511() { checkIsSubtype("int[]","int[]"); }
	@Test public void test_526() { checkNotSubtype("int[]","null[][]"); }
	@Test public void test_527() { checkNotSubtype("int[]","int[][]"); }
	@Test public void test_529() { checkNotSubtype("int[]","null"); }
	@Test public void test_530() { checkNotSubtype("int[]","int"); }
	@Test public void test_535() { checkNotSubtype("int[]","null"); }
	@Test public void test_537() { checkNotSubtype("int[]","null"); }
	@Test public void test_538() { checkNotSubtype("int[]","null|int"); }
	@Test public void test_539() { checkNotSubtype("int[]","int"); }
	@Test public void test_541() { checkNotSubtype("int[]","int|null"); }
	@Test public void test_542() { checkNotSubtype("int[]","int"); }
	@Test public void test_549() { checkNotSubtype("int[]","null[]|null"); }
	@Test public void test_550() { checkNotSubtype("int[]","int[]|int"); }
	@Test public void test_1252() { checkNotSubtype("null[][]","null"); }
	@Test public void test_1253() { checkNotSubtype("null[][]","int"); }
	@Test public void test_1260() { checkNotSubtype("null[][]","null[]"); }
	@Test public void test_1261() { checkNotSubtype("null[][]","int[]"); }
	@Test public void test_1276() { checkIsSubtype("null[][]","null[][]"); }
	@Test public void test_1277() { checkNotSubtype("null[][]","int[][]"); }
	@Test public void test_1279() { checkNotSubtype("null[][]","null"); }
	@Test public void test_1280() { checkNotSubtype("null[][]","int"); }
	@Test public void test_1285() { checkNotSubtype("null[][]","null"); }
	@Test public void test_1287() { checkNotSubtype("null[][]","null"); }
	@Test public void test_1288() { checkNotSubtype("null[][]","null|int"); }
	@Test public void test_1289() { checkNotSubtype("null[][]","int"); }
	@Test public void test_1291() { checkNotSubtype("null[][]","int|null"); }
	@Test public void test_1292() { checkNotSubtype("null[][]","int"); }
	@Test public void test_1299() { checkNotSubtype("null[][]","null[]|null"); }
	@Test public void test_1300() { checkNotSubtype("null[][]","int[]|int"); }
	@Test public void test_1302() { checkNotSubtype("int[][]","null"); }
	@Test public void test_1303() { checkNotSubtype("int[][]","int"); }
	@Test public void test_1310() { checkNotSubtype("int[][]","null[]"); }
	@Test public void test_1311() { checkNotSubtype("int[][]","int[]"); }
	@Test public void test_1326() { checkNotSubtype("int[][]","null[][]"); }
	@Test public void test_1327() { checkIsSubtype("int[][]","int[][]"); }
	@Test public void test_1329() { checkNotSubtype("int[][]","null"); }
	@Test public void test_1330() { checkNotSubtype("int[][]","int"); }
	@Test public void test_1335() { checkNotSubtype("int[][]","null"); }
	@Test public void test_1337() { checkNotSubtype("int[][]","null"); }
	@Test public void test_1338() { checkNotSubtype("int[][]","null|int"); }
	@Test public void test_1339() { checkNotSubtype("int[][]","int"); }
	@Test public void test_1341() { checkNotSubtype("int[][]","int|null"); }
	@Test public void test_1342() { checkNotSubtype("int[][]","int"); }
	@Test public void test_1349() { checkNotSubtype("int[][]","null[]|null"); }
	@Test public void test_1350() { checkNotSubtype("int[][]","int[]|int"); }
	@Test public void test_1402() { checkIsSubtype("null","null"); }
	@Test public void test_1403() { checkNotSubtype("null","int"); }
	@Test public void test_1410() { checkNotSubtype("null","null[]"); }
	@Test public void test_1411() { checkNotSubtype("null","int[]"); }
	@Test public void test_1426() { checkNotSubtype("null","null[][]"); }
	@Test public void test_1427() { checkNotSubtype("null","int[][]"); }
	@Test public void test_1429() { checkIsSubtype("null","null"); }
	@Test public void test_1430() { checkNotSubtype("null","int"); }
	@Test public void test_1435() { checkIsSubtype("null","null"); }
	@Test public void test_1437() { checkIsSubtype("null","null"); }
	@Test public void test_1438() { checkNotSubtype("null","null|int"); }
	@Test public void test_1439() { checkNotSubtype("null","int"); }
	@Test public void test_1441() { checkNotSubtype("null","int|null"); }
	@Test public void test_1442() { checkNotSubtype("null","int"); }
	@Test public void test_1449() { checkNotSubtype("null","null[]|null"); }
	@Test public void test_1450() { checkNotSubtype("null","int[]|int"); }
	@Test public void test_1452() { checkNotSubtype("int","null"); }
	@Test public void test_1453() { checkIsSubtype("int","int"); }
	@Test public void test_1460() { checkNotSubtype("int","null[]"); }
	@Test public void test_1461() { checkNotSubtype("int","int[]"); }
	@Test public void test_1476() { checkNotSubtype("int","null[][]"); }
	@Test public void test_1477() { checkNotSubtype("int","int[][]"); }
	@Test public void test_1479() { checkNotSubtype("int","null"); }
	@Test public void test_1480() { checkIsSubtype("int","int"); }
	@Test public void test_1485() { checkNotSubtype("int","null"); }
	@Test public void test_1487() { checkNotSubtype("int","null"); }
	@Test public void test_1488() { checkNotSubtype("int","null|int"); }
	@Test public void test_1489() { checkIsSubtype("int","int"); }
	@Test public void test_1491() { checkNotSubtype("int","int|null"); }
	@Test public void test_1492() { checkIsSubtype("int","int"); }
	@Test public void test_1499() { checkNotSubtype("int","null[]|null"); }
	@Test public void test_1500() { checkNotSubtype("int","int[]|int"); }
	@Test public void test_1702() { checkIsSubtype("null","null"); }
	@Test public void test_1703() { checkNotSubtype("null","int"); }
	@Test public void test_1710() { checkNotSubtype("null","null[]"); }
	@Test public void test_1711() { checkNotSubtype("null","int[]"); }
	@Test public void test_1726() { checkNotSubtype("null","null[][]"); }
	@Test public void test_1727() { checkNotSubtype("null","int[][]"); }
	@Test public void test_1729() { checkIsSubtype("null","null"); }
	@Test public void test_1730() { checkNotSubtype("null","int"); }
	@Test public void test_1735() { checkIsSubtype("null","null"); }
	@Test public void test_1737() { checkIsSubtype("null","null"); }
	@Test public void test_1738() { checkNotSubtype("null","null|int"); }
	@Test public void test_1739() { checkNotSubtype("null","int"); }
	@Test public void test_1741() { checkNotSubtype("null","int|null"); }
	@Test public void test_1742() { checkNotSubtype("null","int"); }
	@Test public void test_1749() { checkNotSubtype("null","null[]|null"); }
	@Test public void test_1750() { checkNotSubtype("null","int[]|int"); }
	@Test public void test_1802() { checkIsSubtype("null","null"); }
	@Test public void test_1803() { checkNotSubtype("null","int"); }
	@Test public void test_1810() { checkNotSubtype("null","null[]"); }
	@Test public void test_1811() { checkNotSubtype("null","int[]"); }
	@Test public void test_1826() { checkNotSubtype("null","null[][]"); }
	@Test public void test_1827() { checkNotSubtype("null","int[][]"); }
	@Test public void test_1829() { checkIsSubtype("null","null"); }
	@Test public void test_1830() { checkNotSubtype("null","int"); }
	@Test public void test_1835() { checkIsSubtype("null","null"); }
	@Test public void test_1837() { checkIsSubtype("null","null"); }
	@Test public void test_1838() { checkNotSubtype("null","null|int"); }
	@Test public void test_1839() { checkNotSubtype("null","int"); }
	@Test public void test_1841() { checkNotSubtype("null","int|null"); }
	@Test public void test_1842() { checkNotSubtype("null","int"); }
	@Test public void test_1849() { checkNotSubtype("null","null[]|null"); }
	@Test public void test_1850() { checkNotSubtype("null","int[]|int"); }
	@Test public void test_1852() { checkIsSubtype("null|int","null"); }
	@Test public void test_1853() { checkIsSubtype("null|int","int"); }
	@Test public void test_1860() { checkNotSubtype("null|int","null[]"); }
	@Test public void test_1861() { checkNotSubtype("null|int","int[]"); }
	@Test public void test_1876() { checkNotSubtype("null|int","null[][]"); }
	@Test public void test_1877() { checkNotSubtype("null|int","int[][]"); }
	@Test public void test_1879() { checkIsSubtype("null|int","null"); }
	@Test public void test_1880() { checkIsSubtype("null|int","int"); }
	@Test public void test_1885() { checkIsSubtype("null|int","null"); }
	@Test public void test_1887() { checkIsSubtype("null|int","null"); }
	@Test public void test_1888() { checkIsSubtype("null|int","null|int"); }
	@Test public void test_1889() { checkIsSubtype("null|int","int"); }
	@Test public void test_1891() { checkIsSubtype("null|int","int|null"); }
	@Test public void test_1892() { checkIsSubtype("null|int","int"); }
	@Test public void test_1899() { checkNotSubtype("null|int","null[]|null"); }
	@Test public void test_1900() { checkNotSubtype("null|int","int[]|int"); }
	@Test public void test_1902() { checkNotSubtype("int","null"); }
	@Test public void test_1903() { checkIsSubtype("int","int"); }
	@Test public void test_1910() { checkNotSubtype("int","null[]"); }
	@Test public void test_1911() { checkNotSubtype("int","int[]"); }
	@Test public void test_1926() { checkNotSubtype("int","null[][]"); }
	@Test public void test_1927() { checkNotSubtype("int","int[][]"); }
	@Test public void test_1929() { checkNotSubtype("int","null"); }
	@Test public void test_1930() { checkIsSubtype("int","int"); }
	@Test public void test_1935() { checkNotSubtype("int","null"); }
	@Test public void test_1937() { checkNotSubtype("int","null"); }
	@Test public void test_1938() { checkNotSubtype("int","null|int"); }
	@Test public void test_1939() { checkIsSubtype("int","int"); }
	@Test public void test_1941() { checkNotSubtype("int","int|null"); }
	@Test public void test_1942() { checkIsSubtype("int","int"); }
	@Test public void test_1949() { checkNotSubtype("int","null[]|null"); }
	@Test public void test_1950() { checkNotSubtype("int","int[]|int"); }
	@Test public void test_2002() { checkIsSubtype("int|null","null"); }
	@Test public void test_2003() { checkIsSubtype("int|null","int"); }
	@Test public void test_2010() { checkNotSubtype("int|null","null[]"); }
	@Test public void test_2011() { checkNotSubtype("int|null","int[]"); }
	@Test public void test_2026() { checkNotSubtype("int|null","null[][]"); }
	@Test public void test_2027() { checkNotSubtype("int|null","int[][]"); }
	@Test public void test_2029() { checkIsSubtype("int|null","null"); }
	@Test public void test_2030() { checkIsSubtype("int|null","int"); }
	@Test public void test_2035() { checkIsSubtype("int|null","null"); }
	@Test public void test_2037() { checkIsSubtype("int|null","null"); }
	@Test public void test_2038() { checkIsSubtype("int|null","null|int"); }
	@Test public void test_2039() { checkIsSubtype("int|null","int"); }
	@Test public void test_2041() { checkIsSubtype("int|null","int|null"); }
	@Test public void test_2042() { checkIsSubtype("int|null","int"); }
	@Test public void test_2049() { checkNotSubtype("int|null","null[]|null"); }
	@Test public void test_2050() { checkNotSubtype("int|null","int[]|int"); }
	@Test public void test_2052() { checkNotSubtype("int","null"); }
	@Test public void test_2053() { checkIsSubtype("int","int"); }
	@Test public void test_2060() { checkNotSubtype("int","null[]"); }
	@Test public void test_2061() { checkNotSubtype("int","int[]"); }
	@Test public void test_2076() { checkNotSubtype("int","null[][]"); }
	@Test public void test_2077() { checkNotSubtype("int","int[][]"); }
	@Test public void test_2079() { checkNotSubtype("int","null"); }
	@Test public void test_2080() { checkIsSubtype("int","int"); }
	@Test public void test_2085() { checkNotSubtype("int","null"); }
	@Test public void test_2087() { checkNotSubtype("int","null"); }
	@Test public void test_2088() { checkNotSubtype("int","null|int"); }
	@Test public void test_2089() { checkIsSubtype("int","int"); }
	@Test public void test_2091() { checkNotSubtype("int","int|null"); }
	@Test public void test_2092() { checkIsSubtype("int","int"); }
	@Test public void test_2099() { checkNotSubtype("int","null[]|null"); }
	@Test public void test_2100() { checkNotSubtype("int","int[]|int"); }
	@Test public void test_2402() { checkIsSubtype("null[]|null","null"); }
	@Test public void test_2403() { checkNotSubtype("null[]|null","int"); }
	@Test public void test_2410() { checkIsSubtype("null[]|null","null[]"); }
	@Test public void test_2411() { checkNotSubtype("null[]|null","int[]"); }
	@Test public void test_2426() { checkNotSubtype("null[]|null","null[][]"); }
	@Test public void test_2427() { checkNotSubtype("null[]|null","int[][]"); }
	@Test public void test_2429() { checkIsSubtype("null[]|null","null"); }
	@Test public void test_2430() { checkNotSubtype("null[]|null","int"); }
	@Test public void test_2435() { checkIsSubtype("null[]|null","null"); }
	@Test public void test_2437() { checkIsSubtype("null[]|null","null"); }
	@Test public void test_2438() { checkNotSubtype("null[]|null","null|int"); }
	@Test public void test_2439() { checkNotSubtype("null[]|null","int"); }
	@Test public void test_2441() { checkNotSubtype("null[]|null","int|null"); }
	@Test public void test_2442() { checkNotSubtype("null[]|null","int"); }
	@Test public void test_2449() { checkIsSubtype("null[]|null","null[]|null"); }
	@Test public void test_2450() { checkNotSubtype("null[]|null","int[]|int"); }
	@Test public void test_2452() { checkNotSubtype("int[]|int","null"); }
	@Test public void test_2453() { checkIsSubtype("int[]|int","int"); }
	@Test public void test_2460() { checkNotSubtype("int[]|int","null[]"); }
	@Test public void test_2461() { checkIsSubtype("int[]|int","int[]"); }
	@Test public void test_2476() { checkNotSubtype("int[]|int","null[][]"); }
	@Test public void test_2477() { checkNotSubtype("int[]|int","int[][]"); }
	@Test public void test_2479() { checkNotSubtype("int[]|int","null"); }
	@Test public void test_2480() { checkIsSubtype("int[]|int","int"); }
	@Test public void test_2485() { checkNotSubtype("int[]|int","null"); }
	@Test public void test_2487() { checkNotSubtype("int[]|int","null"); }
	@Test public void test_2488() { checkNotSubtype("int[]|int","null|int"); }
	@Test public void test_2489() { checkIsSubtype("int[]|int","int"); }
	@Test public void test_2491() { checkNotSubtype("int[]|int","int|null"); }
	@Test public void test_2492() { checkIsSubtype("int[]|int","int"); }
	@Test public void test_2499() { checkNotSubtype("int[]|int","null[]|null"); }
	@Test public void test_2500() { checkIsSubtype("int[]|int","int[]|int"); }

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
