type byterec is {byte b}

public export method test():
    byterec r = {b: 00000000b}
    assume ~(r.b) == 11111111b