package eu.software4you.spigot.commandmanager;

public enum CommandResult {
    NONE(null),
    SUCCESS(null),
    FAILURE(null),
    NO_PERMISSION(null),
    WRONG_USAGE(null),
    ONLY_PLAYER(null),
    ONLY_CONSOLE(null),
    TYPE_ERROR(null),
    ;

    private String message;
    private String tempMessage;

    CommandResult(final String message) {
        setMessage(message);
    }

    public String getMessage() {
        return tempMessage != null ? tempMessage() : message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String tempMessage() {
        String tmp = this.tempMessage;
        this.tempMessage = null;
        return tmp;
    }

    public CommandResult replace(String... replacements) {
        for (int i = 0; i < replacements.length; i++) {
            String replacement = replacements[i];
            this.tempMessage = this.message.replace("%" + i, replacement);
        }
        return this;
    }

    public CommandResult replaceFirst(String... replacements) {
        for (int i = 0; i < replacements.length; i++) {
            String replacement = replacements[i];
            this.tempMessage = this.message.replaceFirst("%" + i, replacement);
        }
        return this;
    }
}
