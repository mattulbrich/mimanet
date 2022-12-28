jar=lepton-sim/build/libs/lepton-sim-1.0-all.jar
tool=java -jar ${jar}

SYM_SCH=$(wildcard sym/*.sch)
SYM_SYM=$(patsubst %.sch, %.sym, $(wildcard sym/*.sch)) $(patsubst %.net, %.sym, $(wildcard sym/*.net))
SYM_NET=$(patsubst %.sch, %.net, $(wildcard sym/*.sch))
SYM_SYMNET=$(patsubst %.sch, %.symnet, $(wildcard sym/*.sch))
SYM_NET_PROP=$(patsubst %.props, %.net, $(wildcard sym/*.props))
SYM_SYM_PROP=$(patsubst %.props, %.sym, $(wildcard sym/*.props))
JAVA_CLASS=$(patsubst %.java, %.class, $(wildcard sym/*.java))

all: $(SYM_SYM) $(SYM_NET) $(SYM_NET_PROP) $(SYM_SYM_PROP) $(JAVA_CLASS)

jar:
	cd lepton-sim ; ./gradlew shadowJar

check: $(SYM_NET) $(SYM_NET_PROP)
	for x in sym/*.net; do echo "Check $$x:" ; $(tool) widthcheck $$x || exit; done

%.symnet : %.sch 
	lepton-netlist -g mu $< -o $@

%.net : %.sch $(SYM_SYM)
	lepton-netlist -g mu $< -o $@

%.sym : %.symnet 
	$(tool) sym $< > $@

clean:
	rm -f $(wildcard sym/*.net) $(wildcard sym/*.symnet) $(wildcard sym/*.sym) sym/*.class

%.net: %.props
	${tool} compile $< $@

%.sym: %.net %.props
	$(tool) sym $< > $@

tool:
	echo 'export t="${tool}"'

%.class: %.java
	javac --release 11 -g -cp ${jar} $<

test.json: all
	$(tool) simulate sym/TEST.net test.json

fakeClasses:
	cp sym/*.java lepton-sim/src/main/java
