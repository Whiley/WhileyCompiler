type Proc is &{
    function func(bool,int)->bool
}

method test(Proc _this, bool arg) -> bool:
    return _this->func(arg)
