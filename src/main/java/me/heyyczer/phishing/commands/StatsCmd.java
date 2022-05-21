package me.heyyczer.phishing.commands;

import me.heyyczer.phishing.Main;
import me.heyyczer.phishing.database.DatabaseMethods;
import me.heyyczer.phishing.utils.lib.commands.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;
import java.time.Instant;

public class StatsCmd implements ICommand {

    @Override
    public void handle(SlashCommandEvent event) {
        int times = DatabaseMethods.getProtectedTimes(event.getGuild().getId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode("#2F3136"));
        eb.setAuthor("Estatísticas - Proteção Anti-Phishing", null, event.getJDA().getSelfUser().getAvatarUrl());
        eb.addBlankField(false);
        eb.addField("Usuários expulsos", times + " usuário(s)", false);
        eb.setTimestamp(Instant.now());
        eb.setThumbnail(event.getGuild().getIconUrl());
        event.getHook().editOriginalEmbeds(eb.build()).queue(null, Main.errorHandler);
    }

    @Override
    public String getName() {
        return "protectionstats";
    }

    @Override
    public String getDescription() {
        return "🍁 | Verificar estatísticas sobre a proteção Anti-Phishing";
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_SERVER;
    }

    @Override
    public boolean isEphemeral() {
        return true;
    }

}
