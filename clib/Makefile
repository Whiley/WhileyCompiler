# Makefile made by metamake run in /am/rialto/home1/protin/wy/clib

CFLAGS := -ggdb $(CFLAGS)

OBJS :=  wycc_FOM.o wycc__abs.o wycc__file.o wycc__isDigit.o wycc__isLetter.o wycc__isLowerCase.o wycc__isUpperCase.o wycc__isWhiteSpace.o wycc__isqrt.o wycc__max.o wycc__min.o wycc__toUnsignedByte.o wycc__toUnsignedInt.o wycc_indirect_invoke.o wycc_lib.o wycc_main.o

install :	wycc_lib.a
	cp $< ../lib

wycc_lib.a :	$(OBJS)
	ar -rc $@ $(OBJS)



wycc_FOM.o: wycc_FOM.c ../lib/wycc_lib.h common.h FOM.h ytype.h box.h

wycc__abs.o: wycc__abs.c ../lib/wycc_lib.h common.h box.h

wycc__file.o: wycc__file.c ../lib/wycc_lib.h common.h box.h

wycc__isDigit.o: wycc__isDigit.c ../lib/wycc_lib.h common.h box.h

wycc__isLetter.o: wycc__isLetter.c ../lib/wycc_lib.h common.h box.h

wycc__isLowerCase.o: wycc__isLowerCase.c ../lib/wycc_lib.h common.h box.h

wycc__isUpperCase.o: wycc__isUpperCase.c ../lib/wycc_lib.h common.h box.h

wycc__isWhiteSpace.o: wycc__isWhiteSpace.c ../lib/wycc_lib.h common.h \
 box.h

wycc__isqrt.o: wycc__isqrt.c ../lib/wycc_lib.h common.h box.h

wycc__max.o: wycc__max.c ../lib/wycc_lib.h common.h box.h

wycc__min.o: wycc__min.c ../lib/wycc_lib.h common.h box.h

wycc__toUnsignedByte.o: wycc__toUnsignedByte.c ../lib/wycc_lib.h common.h \
 box.h

wycc__toUnsignedInt.o: wycc__toUnsignedInt.c ../lib/wycc_lib.h common.h \
 box.h

wycc_indirect_invoke.o: wycc_indirect_invoke.c ../lib/wycc_lib.h common.h \
 FOM.h box.h

wycc_lib.o: wycc_lib.c ../lib/wycc_lib.h common.h FOM.h ytype.h box.h

wycc_main.o: wycc_main.c ../lib/wycc_lib.h common.h FOM.h

