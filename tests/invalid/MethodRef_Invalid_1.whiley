import * from whiley.lang.*

type FileReader is {int position, [byte] data}

type Reader is {int ::(FileReader, int) read}
