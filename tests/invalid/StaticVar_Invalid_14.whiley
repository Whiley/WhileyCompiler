int var = 0

public export method test():
    // Should not be possible to infer this, since var 
    // could have been changed by any execution traces
    // arising prior to this method being called.    
    assert var == 0    