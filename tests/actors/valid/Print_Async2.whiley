import * from whiley.lang.*

define Actor as ref { int state }

// Tests that an asynchronous message send does not block the sender.
void ::main(Console sys):
    actor = new { state: 6 }
    sys.out!println(actor->state)
    actor!setState(actor->state)
    sys.out.println(actor->state)

void Actor::setState(int state):
    sleep(1000)
    this->state = state
