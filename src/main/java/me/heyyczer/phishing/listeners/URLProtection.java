package me.heyyczer.phishing.listeners;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.NonNull;
import me.heyyczer.phishing.Main;
import me.heyyczer.phishing.database.DatabaseMethods;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class URLProtection extends ListenerAdapter {

    public static final Cache<String, String> cache = Caffeine.newBuilder().maximumSize(10_000).build();

    @Override
    public void onGuildMessageReceived(@NonNull GuildMessageReceivedEvent event) {
        if(event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) return;

        for(Map.Entry<String, String> bannedURL : cache.asMap().entrySet()) {
            if(event.getMessage().getContentRaw().toLowerCase().contains(bannedURL.getKey().toLowerCase()) && event.getMessage().getContentRaw().toLowerCase().contains("http")) {
                event.getMessage().delete().queue(null, Main.errorHandler);

                MessageBuilder mb = new MessageBuilder();
                mb.setContent(event.getAuthor().getAsMention());

                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(Color.RED);
                eb.setDescription("Tentou disseminar um link proibido e foi automaticamente banido.\n**Motivo:** " + bannedURL.getValue());
                mb.setEmbeds(eb.build());
                event.getChannel().sendMessage(mb.build()).queue(msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS, null, Main.errorHandler));

                eb.addField("Mensagem enviada", event.getMessage().getContentRaw(), false);
                eb.addField("Canal enviado", event.getChannel().getAsMention(), false);
                eb.addField("Contexto proibido detectado:", bannedURL.getValue(), false);
                mb.setEmbeds(eb.build());

                Object reportChannel = DatabaseMethods.getConfig(event.getGuild().getId(), "urlProtectionChannel");
                if(reportChannel != null)
                    event.getJDA().getTextChannelById((String) reportChannel).sendMessage(mb.build()).queue();

                event.getAuthor().openPrivateChannel().queue(channel -> {
                    EmbedBuilder info = new EmbedBuilder();
                    info.setColor(Color.decode("#2F3136"));
                    info.setAuthor("Automaticamente expulso", null, event.getGuild().getIconUrl());
                    info.setDescription("Olá " + event.getAuthor().getAsMention() + ", você foi **automaticamente expulso** do servidor ``" + event.getGuild().getName() + "`` por disseminar um link ou conteúdo malicioso (" + bannedURL.getValue() + ").");
                    info.addField("🛡 Segurança", "O banimento foi realizado de forma automática, visando proteger outros membros de perderem suas contas de Discord e/ou Steam.", false);
                    info.addField("⚔ Recuperação", "Imaginamos que sua conta tenha sido roubada (provavelmente por ter clicado em um destes links e inserido sua conta), entre em contato com ``" + event.getGuild().getOwner().getUser().getAsTag() + "`` pelo Discord, para solicitar um novo convite.", false);
                    info.addField("", "", false);
                    info.setThumbnail("https://cdn.dribbble.com/users/1263024/screenshots/3221996/ochroniarz.gif");
                    channel.sendMessageEmbeds(info.build()).queue();
                }, Main.errorHandler);

                event.getGuild().kick(event.getMember(), "[" + event.getJDA().getSelfUser().getAsTag() + "] Proteção de Conteúdo - " + bannedURL.getValue()).queueAfter(2, TimeUnit.SECONDS, a ->
                    DatabaseMethods.registerKick(event.getMember().getId(), event.getGuild().getId(), bannedURL.getKey()),
                    Main.errorHandler
                );
                break;
            }
        }
    }

}
