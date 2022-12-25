package de.matul.lepton_sim.sim;

public enum Signal {
    ZERO('0'), ONE('1'), ERROR('X'), HIGH_IMP('Z');

    private static final Signal[][] MERGER = new Signal[][] {
            { ZERO,  ERROR, ERROR, ZERO },
            { ERROR, ONE,   ERROR, ONE },
            { ERROR, ERROR, ERROR, ERROR },
            { ZERO,  ONE,   ERROR, HIGH_IMP }
    };
    private final char charRep;

    Signal(char charRep) {
        this.charRep = charRep;
    }

    Signal merge(Signal other) {
        return MERGER[ordinal()][other.ordinal()];
    }

    public char toChar() {
        return charRep;
    }
}
