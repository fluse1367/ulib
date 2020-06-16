package eu.software4you.ulib;

public enum RunMode {
    SPIGOT("spigot"), BUNGEECORD("bungeecord"), STANDALONE("standalone");
    private final String name;

    RunMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
