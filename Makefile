###################################################

ChatServer	=	"ChatServer/build/ChatServer.jar"
ChatServerPath	=	"ChatServer/"

ChatClient	=	"ChatClient/build/ChatClient.jar"
ChatClientPath	=	"ChatClient/"

Library		=	"lib/build/ChatProtocol.jar"
LibraryPath	=	"lib/"

BUILDPATH	=	"build/"

###################################################

all: $(ChatServer) $(ChatClient)
	cp $(ChatServer) $(BUILDPATH)
	cp $(ChatClient) $(BUILDPATH)
	cp $(Library) $(BUILDPATH)
	@echo "compiled everything succesful"

	
clean:
	cd $(ChatServerPath) && make clean
	cd $(ChatClientPath) && make clean
	cd $(LibraryPath) && make clean
	rm --force $(BUILDPATH)/*.jar

install: 


uninstall:

###################################################

$(ChatServer): $(Library)
	@cd $(ChatServerPath) && make
	@echo "\nChatServer compiled successful\n"

$(ChatClient): $(Library)
	@cd $(ChatClientPath) && make
	@echo "\nChatClient compiled successful\n"

$(Library):
	@cd $(LibraryPath) && make
	@echo "\nChatProtocol compiled successful\n"

