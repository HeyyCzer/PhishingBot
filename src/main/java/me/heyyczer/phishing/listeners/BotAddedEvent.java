package me.heyyczer.phishing.listeners;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class BotAddedEvent extends ListenerAdapter {

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        Logger logger = Logger.getLogger("GuildJoin");
//        event.getGuild().getTextChannels().get(0).createInvite().queue(s ->
//                logger.info("[🦜] Convite criado: " + s.getUrl() + " (" + event.getGuild().getName() + ")"),
//                Main.errorHandler
//        );
        logger.info("[🦜] Fui adicionado ao servidor " + event.getGuild().getName() + " (" + event.getGuild().getId() + ").");
    }

}
