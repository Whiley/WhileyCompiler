import println from whiley.lang.System
import * from whiley.lang.Int

int g(int x) ensures $ > 0 && $ < 125:
    return 1

[i8] f(int x):
    return [g(x)]

