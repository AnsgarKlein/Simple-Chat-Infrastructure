###################################################

SOURCEPATH	=	src
LIBPATH		=	../lib
BUILDPATH	=	build

JC		=	javac
JFLAGS		=	-cp $(LIBPATH):$(SOURCEPATH)

SOURCES		=	$(wildcard $(SOURCEPATH)/*.java)
CLASSES		=	$(SOURCES:.java=.class)

###################################################

all: $(CLASSES) $(BUILDPATH)
	@echo "creating .jar file...\n"
	@chmod +x createjar.sh
	@./createjar.sh

clean:
	rm --force $(CLASSES) 
	rm --force $(SOURCEPATH)/*.class
	rm --force $(BUILDPATH)/*.class
	rm --force $(BUILDPATH)/*.jar

###################################################

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $<

$(BUILDPATH):
	mkdir -p $(BUILDPATH)

###################################################
