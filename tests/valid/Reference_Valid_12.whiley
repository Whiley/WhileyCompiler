// Attempt to break JavaScript handling of native references.
public export method test():
    int[] xs = [1,2,3]
    // Create pointer to distinct value
    &(int[]) ptr = new xs
    // Needed for verification
    assume |*ptr| == 3
    // Update distinct value
    (*ptr)[0] = 123
    // Check what's changed
    assert xs[0] == 1
    assert xs[1] == 2
    assert xs[2] == 3
    assume (*ptr)[0] == 123
    assume (*ptr)[1] == 2
    assume (*ptr)[2] == 3    
    
    