package eu.software4you.spigot.multiversion;

public enum Protocol {
    // 1.7
    v1_7_R1(4, "1.7.2"),
    v1_7_R2(4, "1.7.5"),
    v1_7_R3(5, "1.7.8"),
    v1_7_R4(5, "1.7.9", "1.7.10"),
    // 1.8
    v1_8_R1(47, "1.8"),
    v1_8_R2(47, "1.8.3"),
    v1_8_R3(47, "1.8.4", "1.8.5", "1.8.6", "1.8.7", "1.8.8"),
    // 1.9
    v1_9_R1(109, "1.9", "1.9.2"),
    v1_9_R2(110, "1.9.4"),
    // 1.10
    v1_10_R1(210, "1.10", "1.10.2"),
    // 1.11
    v1_11_R1(316, "1.11", "1.11.1", "1.11.2"),
    // 1.12
    v1_12_R1(340, "1.12", "1.12.1", "1.12.2"),
    // 1.13
    v1_13_R1(401, "1.13"),
    v1_13_R2(404, "1.13.1", "1.13.2"),
    // 1.14
    v1_14_R1(498, "1.14", "1.14.1", "1.14.2", "1.14.3", "1.14.4"),
    // 1.15
    v1_15_R1(575, "1.15", "1.15.1", "1.15.2"),
    // 1.16
    v1_16_R1(736, "1.16.1"),
    v1_16_R2(753, "1.16.2", "1.16.3"),
    v1_16_R3(754, "1.16.4", "1.16.5"),
    // 1.17
    v1_17_R1(756, "1.17", "1.17.1"),
    // unknown
    UNKNOWN(-1);

    final int protocol;
    final String[] versions;

    Protocol(int protocol, String... versions) {
        this.protocol = protocol;
        this.versions = versions;
    }

    public String getVersion() {
        return versions.length == 0 ? null : versions[versions.length - 1];
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
