type obj_xy is {int x, int y, ...}
type obj_x is {int x, ...}
type obj_y is {int y, ...}

function f(obj_xy o) -> obj_x|obj_y:
    return o