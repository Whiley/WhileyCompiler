type Proc is &{
    function func(real,int)->real
}

method test(Proc this, real arg) -> real:
    return this->func(arg)
