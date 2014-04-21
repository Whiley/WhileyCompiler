method swap(&int x_ptr, &int y_ptr):
    int tmp = *x_ptr
    *x_ptr = *y_ptr
    *y_ptr = tmp

method main(System.Console console):
    &int x = new 1
    &int y = new 2
    console.out.println("*x = " ++ *x)
    console.out.println("*y = " ++ *y)
    swap(x,y)
    console.out.println("*x = " ++ *x)
    console.out.println("*y = " ++ *y)
    

