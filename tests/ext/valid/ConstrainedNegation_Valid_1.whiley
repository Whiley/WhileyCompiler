import println from whiley.lang.System

define nat as int where $>=0
define neg as !nat

int f(neg x) ensures $ < 0:
    return x

public void ::main(System.Console sys):
    sys.out.println(f(-1))
    sys.out.println(f(-2))
