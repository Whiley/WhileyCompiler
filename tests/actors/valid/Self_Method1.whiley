import toString from whiley.lang.Any
import * from whiley.lang.System

// Tests that calling an internal method correctly yields.
define Actor as process { int x }

void ::main(System sys, [string] args):
    act = spawn { x: 1 }
    act!run(sys.out)

void Actor::run(SystemOutWriter out):
    this.self(out)
    out.println(toString(this.x))

void Actor::self(SystemOutWriter out):
    out.println(toString(this.x))
    this.x = 2
