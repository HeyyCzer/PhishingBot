package me.heyyczer.phishing.utils.lib.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;

public interface ICommand {

    void handle(SlashCommandEvent event);

    String getName();

    String getDescription();

    default List<OptionData> getArguments() {
        return Collections.emptyList();
    }

    default Permission getPermission() {
        return null;
    }

    default boolean isEphemeral() {
        return false;
    }

}
