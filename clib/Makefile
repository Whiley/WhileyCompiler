#../lib/wycc_lib.o:	../lib/wycc_lib.h ./wycc_lib.c
#	gcc -ggdb -c wycc_lib.c
#	#mv wycc_lib.o ../lib

OBJS =	\
wycc_lib.o \
wycc_main.o \
wycc_FOM.o \
wycc_indirect_invoke.o \
wycc__abs.o \
wycc__min.o \
wycc__max.o \
wycc__isqrt.o \
wycc__isDigit.o \
wycc__isLetter.o \
wycc__isWhiteSpace.o \
wycc__isLowerCase.o \
wycc__isUpperCase.o \
wycc__toUnsignedInt.o \
wycc__toUnsignedByte.o

wycc_lib.a:	$(OBJS)
	ar -rc wycc_lib.a $(OBJS)
	cp wycc_lib.a ../lib

wycc_lib.o:	../lib/wycc_lib.h common.h ytype.h wycc_lib.c

wycc_main.o:	../lib/wycc_lib.h common.h FOM.h wycc_main.c

wycc_FOM.o:	../lib/wycc_lib.h common.h FOM.h ytype.h wycc_FOM.c

wycc_indirect_invoke.o:	../lib/wycc_lib.h common.h FOM.h box.h \
		 wycc_indirect_invoke.c

wycc__abs.o:	../lib/wycc_lib.h common.h box.h wycc__abs.c

wycc__max.o:	../lib/wycc_lib.h common.h box.h wycc__max.c

wycc__min.o:	../lib/wycc_lib.h common.h box.h wycc__min.c

wycc__isqrt.o:	../lib/wycc_lib.h common.h box.h wycc__isqrt.c

wycc__isDigit.o:	../lib/wycc_lib.h common.h box.h wycc__isDigit.c

wycc__isWhiteSpace.o:	../lib/wycc_lib.h common.h box.h wycc__isWhiteSpace.c

wycc__isLetter.o:	../lib/wycc_lib.h common.h box.h wycc__isLetter.c

wycc__isLowerCase.o:	../lib/wycc_lib.h common.h box.h wycc__isLowerCase.c

wycc__isUpperCase.o:	../lib/wycc_lib.h common.h box.h wycc__isUpperCase.c

wycc__toUnsignedInt.o:	../lib/wycc_lib.h common.h box.h wycc__toUnsignedInt.c

wycc__toUnsignedByte.o:	../lib/wycc_lib.h common.h box.h wycc__toUnsignedByte.c

