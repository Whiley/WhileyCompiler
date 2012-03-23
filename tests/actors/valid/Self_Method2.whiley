import * from whiley.lang.*

define Actor as ref { int state }

void ::main(Console sys):
    act = new { state: 5 }
    act.method(sys)

// Tests that calling an internal method as an expression correctly yields.
void Actor::method(Console sys):
    sys.out!println(this.state())

int Actor::state():
    return this->state
