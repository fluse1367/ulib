package eu.software4you.minecraft.multiversion.protocol;

public enum Protocol {
    v1_7_R1(4), v1_7_R2(4), v1_7_R3(5), v1_7_R4(5),
    v1_8_R1(47), v1_8_R2(47), v1_8_R3(47),
    v1_9_R1(109), v1_9_R2(110),
    v1_10_R1(210),
    v1_11_R1(316),
    v1_12_R1(340),
    v1_13_R1(401), v1_13_R2(404),
    v1_14_R1(498),
    v1_15_R1(575),
    v1_16_R1(736),
    UNKNWON(-1);
    final int protocol;

    Protocol(int protocol) {
        this.protocol = protocol;
    }

    public boolean above(Protocol toCompare) {
        return protocol > toCompare.protocol;
    }

    public boolean atLeast(Protocol toCompare) {
        return protocol >= toCompare.protocol;
    }

    public boolean below(Protocol toCompare) {
        return protocol < toCompare.protocol;
    }

    public boolean atTheMost(Protocol toCompare) {
        return protocol <= toCompare.protocol;
    }
}
