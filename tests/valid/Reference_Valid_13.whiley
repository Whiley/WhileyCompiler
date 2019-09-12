type Point is {int x, int y}
// Attempt to break JavaScript handling of native references.
public export method test():
    Point p = {x:1,y:2}
    // Create pointer to distinct value
    &Point ptr = new p
    // Update distinct value
    ptr->x = 123
    // Check what's changed
    assert p.x == 1
    assert p.y == 2
    assert ptr->x == 123
    assume ptr->y == 2
    
    