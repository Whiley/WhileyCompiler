function f(bool[] ps) -> int:
   return |ps|

function f(int[] xs) -> int:
  return |xs|

public export method test():
   int[] xs = []
   assume f(xs) == 0