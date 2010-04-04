
# This makefile uses features only found in GNU make.
# Please ensure you are using GNU make (sometimes installed as 'gmake').

# If you create a new directory for source files, be sure to put it here!
SRCDIRS = pictcon/com pictcon/dialogs pictcon/gamewindow pictcon/paperwindow pictcon/queue pictcon .

# Programs.
JAVA = java
JAVAC = javac -deprecation

# Get all the .java files in the source directories.
JAVAFILES = $(foreach dir,$(SRCDIRS),$(wildcard $(dir)/*.java))

# Create a target .class file for each source .java file.
CLASSFILES = $(JAVAFILES:.java=.class)

# By default, build all the classes.
all: $(CLASSFILES)

# Build and run.
run: all
	$(JAVA) PictorialConsequences

# Rule for compiling java source.
%.class: %.java
	$(JAVAC) $<

# Remove all rebuildable files.
#clean:
#	$(RM) \
#		$(foreach file,$(foreach dir,$(SRCDIRS),$(wildcard $(dir)/*.class)),'$(file)') \
#		$(foreach file,$(foreach dir,$(SRCDIRS),$(wildcard $(dir)/*~)),'$(file)')
clean:
	$(RM) $(foreach dir,$(SRCDIRS),$(dir)/*.class)
	$(RM) $(foreach dir,$(SRCDIRS),$(dir)/*~)

jar: all
	$(RM) pictcon.jar
	jar cvmfe pictcon.mf pictcon.jar PictorialConsequences images/ pictcon/ *.class
