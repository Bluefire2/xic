XIC_BUILD=./xic-build
XTH_BIN=./xth/xth
XTH_TEST_DIR=./xth/tests/pa1
XTH_SCRIPT=./xth/tests/pa1/xthScript
GRADLE_SETUP_FILES=build.gradle settings.gradle gradlew make_jar_executable.sh gradle

.PHONY: build

default: build

build:
	$(XIC_BUILD)

test:
	$(XTH_BIN) -compilerpath . -testpath $(XTH_TEST_DIR) $(XTH_SCRIPT)

zip:
	zip -r xic.zip $(GRADLE_SETUP_FILES) lib Makefile src xic-build

clean:
	rm -rf xic.zip build .gradle xic ~/bin/xic
	rm -f src/main/java/lexer/XiLexer.java*
