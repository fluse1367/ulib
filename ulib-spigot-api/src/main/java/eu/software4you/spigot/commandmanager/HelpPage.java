package eu.software4you.spigot.commandmanager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class HelpPage {
    private List<CommandHelp> helpPages;
    private List<String> helpText;
    private String command;
    private String header;
    private String footer;

    public HelpPage(String command, String header, String footer) {
        this.helpPages = new ArrayList<CommandHelp>();
        this.helpText = new ArrayList<String>();
        this.command = command;
        this.header = header;
        this.footer = footer;
    }

    public void addPage(String argument, String description) {
        if (argument.isEmpty()) {
            this.helpPages.add(new CommandHelp(this.command, description));
        } else {
            this.helpPages.add(new CommandHelp(this.command + " " + argument, description));
        }
    }

    public void prepare() {
        if (this.helpPages.isEmpty()) {
            return;
        }
        this.helpText.add(header);
        for (CommandHelp ch : this.helpPages) {
            this.helpText.add(ch.getText());
        }
        this.helpText.add(footer);
    }

    public boolean sendHelp(CommandSender s, CommandArguments args) {
        if (args.getLength() == 1 && (args.getString(0).equalsIgnoreCase("?") || args.getString(0).equalsIgnoreCase("help")) && !this.helpText.isEmpty()) {
            sendHelp(s);
            return true;
        }
        return false;
    }

    public void sendHelp(CommandSender s) {
        for (String string : this.helpText) {
            s.sendMessage(string);
        }
    }

    private class CommandHelp {
        private String FULL_TEXT;

        public CommandHelp(String cmd, String description) {
            this.FULL_TEXT = ChatColor.GOLD + cmd + ChatColor.GRAY + " - " + description;
        }

        public String getText() {
            return this.FULL_TEXT;
        }
    }
}
