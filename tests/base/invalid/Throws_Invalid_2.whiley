define Error as {string msg}
define WrongError as {int msg}

int f(int x) throws WrongError:
    if x < 0:
        throw {msg: "error"}
    return 1
        
