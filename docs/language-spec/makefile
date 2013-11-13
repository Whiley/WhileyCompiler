TARGET = whiley-spec
DEPS = introduction.tex syntax.tex

all: $(TARGET).ps $(TARGET).pdf

%.dvi : %.tex $(DEPS)
	@for X in 1 2 3 ; do \
        ( echo "---------------- Latex ($$X) ----------------" && latex $<) \
	done

%.pdf : %.dvi
	@echo "---------------- dvipdf ----------------" && dvipdfm $<

%.ps : %.dvi
	@echo "---------------- dvips ----------------" && dvips -f < $< > $@

clean:
	rm *.bbl *.aux *.dvi *.log *.ps *.pdf *~ *.blg *.bak
