import println from whiley.lang.System

public [int] method([byte] bytes):
	pos = 0
	data= []
	for i in 0..100:
		pos = pos + 1
		data = data +[Byte.toUnsignedInt(bytes[pos])]
        return data

public void ::main(System.Console sys):
    bytes = [0b,1b,11b,111b]
    ints = method(bytes)
    sys.out.println(ints)
