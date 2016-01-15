

type anat is (int x) where x >= 0

type bnat is (int x) where (2 * x) >= x

function atob(anat x) -> bnat:
    return x

function btoa(bnat x) -> anat:
    return x

public export method test() :
    int x = 1
    assume atob(x) == 1
    assume btoa(x) == 1
