function sum([int] xs) -> (int r)
// All elements of parameter xs are greater-or-equal to zero
requires all { x in xs | x >= 0 }
// Return value must be greater-or-equal to zero
ensures r >= 0:
   //
   nat i = 0
   nat r = 0
   //
   while i < |xs|:
       r = r + xs[i]
       i = i + 1
   //
   return r

type nat is (int x) where x >= 0

public export method test():
    assume sum([]) == 0
    assume sum([1]) == 1
    assume sum([1,2]) == 3
