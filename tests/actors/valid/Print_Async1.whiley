import * from whiley.lang.*

// Tests that an asynchronous message send causes the method to be called.
// It is important that the main method exiting doesn't stop the program.
void ::main(Console sys):
    sys.out!println(5)
