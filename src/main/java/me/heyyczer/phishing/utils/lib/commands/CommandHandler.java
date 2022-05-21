package me.heyyczer.phishing.utils.lib.commands;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CommandHandler extends ListenerAdapter {

    private CommandManager manager;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if(manager == null) manager = new CommandManager();
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if(event.getUser().isBot() || event.getTextChannel().getType() == ChannelType.PRIVATE) return;
        manager.handle(event);
    }

}
