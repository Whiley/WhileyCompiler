import * from whiley.lang.*

define Actor as ref { int state }

void ::main(Console sys):
    actor = new { state: 6 }
    actor.method(sys, 5)

// Tests that reading a parameter doesn't affect the actor invariants.
void Actor::method(Console sys, int i):
    sys.out.println(this->state)
    sys.out.println(i)
