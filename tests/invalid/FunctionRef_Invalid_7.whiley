type Proc is &{
    function func(bool,int)->bool
}

method test(Proc this, bool arg) -> bool:
    return this->func(arg)
