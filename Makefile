jar=lepton-sim/build/libs/lepton-sim-1.0-all.jar

LIB_SCH=$(wildcard lib/*.sch)
LIB_SYM=$(patsubst %.sch,%.sym,$(wildcard lib/*.sch))
SYM_SYM=$(patsubst %.sch, %.sym, $(wildcard sym/*.sch))
SYM_NET=$(patsubst %.sch, %.net, $(wildcard sym/*.sch))

all: $(LIB_SYM) $(SYM_SYM) $(SYM_NET)

jar:
	cd lepton-sim ; ./gradlew shadowJar

%.net : %.sch
	lepton-netlist -g mu $< -o $@

%.sym : %.net
	java -jar $(jar) sym $< > $@

clean: 
	rm $(wildcard lib/*.net) $(LIB_SYM)
