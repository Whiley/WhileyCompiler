// Test recursive types

type mymethod is method<a>(&*:mymethod)->(&a:mymethod)

method <a> m (&*:mymethod x) -> &a:mymethod:
    return a:new &m

public export method test():
    &mymethod x = m<*>(new &m)
    mymethod xx = &<a>(&mymethod y -> *(new y))
    xx = &<b>(&mymethod y -> *(new y))
    &this:mymethod y = m<this>(new &m)
    mymethod yy = &<a>(&mymethod z -> *(new z))
    yy = &<b>(&mymethod z -> *(new z))
    a:
        &a:mymethod z = m<a>(new &m)
