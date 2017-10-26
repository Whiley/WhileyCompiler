function isPal(int[] a) -> (bool r)      
requires |a| >= 0
ensures r <==> all { i in 0..|a| | a[i] == a[|a|-i-1] }:
      int lo = 0
      int hi = |a|-1                                                                                                         
      while lo < hi && a[lo] == a[hi]
            where 0 <= lo
            where lo <= hi+1
            where hi <= |a|-1
            where hi == |a|-1-lo
            where all { i in 0..lo | a[i] == a[|a|-i-1] }:
          lo = lo+1
          hi = hi-1
      //
      return lo >= hi

public export method test():
    assume isPal([])
    assume isPal([1])
    assume isPal([1,1])
    assume isPal([1,2,1])
    assume isPal([1,2,3,2,1])
    assume isPal([1,2,3,4,3,2,1])
    // failing
    assume !isPal([1,2])
    assume !isPal([2,1])
    assume !isPal([1,2,3])
    assume !isPal([1,2,3,1])
