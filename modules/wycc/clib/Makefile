# Makefile made by metamake run in /am/rialto/home1/protin/wy/clib

CFLAGS := -ggdb $(CFLAGS)

LIB_DIR := ../../../lib

OBJS :=  wycc_FOM.o wycc__abs.o wycc__file.o wycc__isDigit.o wycc__isLetter.o wycc__isLowerCase.o wycc__isUpperCase.o wycc__isWhiteSpace.o wycc__isqrt.o wycc__max.o wycc__min.o wycc__toUnsignedByte.o wycc__toUnsignedInt.o wycc_indirect_invoke.o wycc_lib.o wycc_main.o wycc_math_stubbed.o wycc_math_gmp.o wyil_deref.o wyil_range.o wyil_strapp.o wyil_listsub.o wyil_listco.o wyil_upd_str.o wyil_set_uni.o wyil_substr.o wyil_set_diff.o wyil_set_int.o wycc_chunk.o wycc_comp.o

install :	wycc_lib.a wycc_math_gmp.o
	cp $^ $(LIB_DIR)

wycc_lib.a :	$(OBJS)
	ar -rc $@ $(OBJS)
	ar -d $@ wycc_math_gmp.o
#	ar -mb wycc_lib.o $@ wycc_math_gmp.o


wycc_FOM.o: wycc_FOM.c ../include/wycc_lib.h common.h FOM.h ytype.h box.h

wycc__abs.o: wycc__abs.c ../include/wycc_lib.h common.h box.h

wycc__file.o: wycc__file.c ../include/wycc_lib.h common.h box.h

wycc__isDigit.o: wycc__isDigit.c ../include/wycc_lib.h common.h box.h

wycc__isLetter.o: wycc__isLetter.c ../include/wycc_lib.h common.h box.h

wycc__isLowerCase.o: wycc__isLowerCase.c ../include/wycc_lib.h common.h box.h

wycc__isUpperCase.o: wycc__isUpperCase.c ../include/wycc_lib.h common.h box.h

wycc__isWhiteSpace.o: wycc__isWhiteSpace.c ../include/wycc_lib.h common.h \
 box.h

wycc__isqrt.o: wycc__isqrt.c ../include/wycc_lib.h common.h box.h

wycc__max.o: wycc__max.c ../include/wycc_lib.h common.h box.h

wycc__min.o: wycc__min.c ../include/wycc_lib.h common.h box.h

wycc__toUnsignedByte.o: wycc__toUnsignedByte.c ../include/wycc_lib.h common.h \
 box.h

wycc__toUnsignedInt.o: wycc__toUnsignedInt.c ../include/wycc_lib.h common.h \
 box.h

wycc_indirect_invoke.o: wycc_indirect_invoke.c ../include/wycc_lib.h common.h \
 FOM.h box.h

wycc_lib.o: wycc_lib.c ../include/wycc_lib.h common.h FOM.h ytype.h box.h math.h

wycc_math_stubbed.o: wycc_math_stubbed.c  ../include/wycc_lib.h common.h FOM.h ytype.h box.h \
 math.h

wycc_math_gmp.o: wycc_math_gmp.c ../include/wycc_lib.h common.h FOM.h ytype.h box.h \
 math.h

wycc_main.o: wycc_main.c ../include/wycc_lib.h common.h FOM.h

wyil_deref.o:	wyil_deref.c ../include/wycc_lib.h common.h box.h

wyil_range.o:	wyil_range.c ../include/wycc_lib.h common.h box.h

wyil_strapp.o:	wyil_strapp.c ../include/wycc_lib.h common.h box.h

wyil_listsub.o:	wyil_listsub.c ../include/wycc_lib.h common.h box.h

wyil_listco.o:	wyil_listco.c ../include/wycc_lib.h common.h box.h

wyil_upd_str.o:	wyil_upd_str.c ../include/wycc_lib.h common.h box.h

wyil_substr.o:	wyil_substr.c ../include/wycc_lib.h common.h box.h

wyil_set_uni.o:	wyil_set_uni.c ../include/wycc_lib.h common.h box.h chunk.h

wyil_set_int.o:	wyil_set_int.c ../include/wycc_lib.h common.h box.h chunk.h

wyil_set_diff.o:	wyil_set_diff.c ../include/wycc_lib.h common.h box.h chunk.h

wycc_chunk.o:	wycc_chunk.c ../include/wycc_lib.h common.h box.h chunk.h

wycc_comp.o:	wycc_comp.c ../include/wycc_lib.h common.h box.h chunk.h


