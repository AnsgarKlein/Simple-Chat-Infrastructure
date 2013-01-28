###################################################

BUILDPATH		=	"build/"

JC			=	javac

CHATSERVER_PATH		=	ChatServer/src
CHATSERVER_SOURCES	=	$(wildcard $(CHATSERVER_PATH)/*.java)
CHATSERVER_CLASSES	=	$(CHATSERVER_SOURCES:.java=.class)

CHATCLIENT_PATH		=	ChatClient/src
CHATCLIENT_SOURCES	=	$(wildcard $(CHATCLIENT_PATH)/*.java)
CHATCLIENT_CLASSES	=	$(CHATCLIENT_SOURCES:.java=.class)

LIBRARY_PATH		=	lib/ChatProtocol
LIBRARY_SOURCES		=	$(wildcard $(LIBRARY_PATH)/*.java)
LIBRARY_CLASSES		=	$(LIBRARY_SOURCES:.java=.class)

###################################################

all: CHATSERVER CHATCLIENT LIBRARY
	@echo "\tcompiled everything succesful\n"

clean:
	rm --force $(CHATSERVER_PATH)/*.class
	rm --force $(CHATCLIENT_PATH)/*.class
	rm --force $(LIBRARY_PATH)/*.class
	rm --force --recursive $(BUILDPATH)/ChatServer
	rm --force --recursive $(BUILDPATH)/ChatClient
	rm --force --recursive $(BUILDPATH)/lib

install: 


uninstall:

###################################################

CHATSERVER: $(CHATSERVER_CLASSES)
	mkdir -p $(BUILDPATH)ChatServer
	cp $(CHATSERVER_PATH)/*.class $(BUILDPATH)ChatServer

$(CHATSERVER_CLASSES): $(CHATSERVER_SOURCES) $(LIBRARY_CLASSES)
	$(JC) -cp lib/:$(CHATSERVER_PATH) $(CHATSERVER_SOURCES)
	@echo "\tChatServer compiled successful\n"

#-------------------------------------------------#

CHATCLIENT: $(CHATCLIENT_CLASSES)
	mkdir -p $(BUILDPATH)ChatClient
	cp $(CHATCLIENT_PATH)/*.class $(BUILDPATH)ChatClient

$(CHATCLIENT_CLASSES): $(CHATCLIENT_SOURCES) $(LIBRARY_CLASSES)
	$(JC) -cp lib/:$(CHATCLIENT_PATH) $(CHATCLIENT_SOURCES)
	@echo "\tChatClient compiled successful\n"

#-------------------------------------------------#

LIBRARY: $(LIBRARY_SOURCES)
	mkdir -p $(BUILDPATH)lib/ChatProtocol
	cp $(LIBRARY_PATH)/*.class $(BUILDPATH)lib/ChatProtocol

$(LIBRARY_CLASSES): $(LIBRARY_SOURCES)
	$(JC) $(JFLAGS) $(LIBRARY_SOURCES)
	@echo "\tChatProtocol compiled successful\n"

#-------------------------------------------------#
