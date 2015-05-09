

function f({int=>any} xs) -> int:
    if xs is {int=>[int]}:
        return 1
    else:
        return -1

public export method test() -> void:
    {int=>any} s1 = {0=>"Hello"}
    {int=>any} s2 = {1=>"Hello"}
    {int=>any} s3 = {0=>"Hello", 1=>"Hello"}
    {int=>any} s4 = {0=>"Hello", 1=>"Hello", 3=>"Hello"}
    assume f(s1) == 1
    assume f(s2) == 1
    assume f(s3) == 1
    assume f(s4) == 1
    {int=>int} t1 = {0=>0, 1=>1}
    assume f(t1) == -1
