define err as {string errmsg}

int|err System::check(int dummy):
    if(dummy > 0):
        return dummy
    else:
        return {errmsg: "Error!"}

void System::main([string] args):
    r = this->check(1)
    

