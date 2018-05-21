type nat is (int n) where n >= 0
type neg is (int n) where n <= 0

type knackered is nat|neg

function f(int x) -> (knackered r):
    return x
