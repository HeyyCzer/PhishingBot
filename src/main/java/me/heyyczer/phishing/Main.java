package me.heyyczer.phishing;

import me.heyyczer.phishing.database.DatabaseConnection;
import me.heyyczer.phishing.database.DatabaseMethods;
import me.heyyczer.phishing.listeners.BotAddedEvent;
import me.heyyczer.phishing.listeners.URLProtection;
import me.heyyczer.phishing.utils.ExceptionManager;
import me.heyyczer.phishing.utils.lib.commands.CommandHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class Main {

    public static JDA jda;
    public static final ErrorHandler errorHandler = new ErrorHandler()
            .ignore(ErrorResponse.EMPTY_MESSAGE,
                    ErrorResponse.VERIFICATION_ERROR,
                    ErrorResponse.SERVER_ERROR,
                    ErrorResponse.UNKNOWN_MEMBER,
                    ErrorResponse.UNKNOWN_MESSAGE,
                    ErrorResponse.MISSING_PERMISSIONS
            );

    public static void main(String[] args) {
        try {
            JDABuilder builder = JDABuilder.createDefault(System.getenv().get("BOT_TOKEN"));
            builder.setAutoReconnect(true);
            builder.setActivity(Activity.watching("Links proibidos"));

            builder.disableCache(
                    CacheFlag.ACTIVITY,
                    CacheFlag.VOICE_STATE,
                    CacheFlag.EMOTE,
                    CacheFlag.ONLINE_STATUS,
                    CacheFlag.CLIENT_STATUS
            );
            builder.setMemberCachePolicy(MemberCachePolicy.OWNER);
            builder.setChunkingFilter(ChunkingFilter.NONE);
            builder.disableIntents(
                    GatewayIntent.GUILD_PRESENCES,
                    GatewayIntent.GUILD_MESSAGE_TYPING,
                    GatewayIntent.GUILD_EMOJIS,
                    GatewayIntent.GUILD_INVITES,
                    GatewayIntent.GUILD_MESSAGE_REACTIONS,
                    GatewayIntent.DIRECT_MESSAGES,
                    GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                    GatewayIntent.DIRECT_MESSAGE_TYPING,
                    GatewayIntent.GUILD_VOICE_STATES
            );
            builder.setLargeThreshold(50);
            builder.addEventListeners(
                    new URLProtection(),
                    new CommandHandler(),
                    new BotAddedEvent()
            );
            jda = builder.build();

            DatabaseConnection.openConnection();

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    URLProtection.cache.cleanUp();
                    DatabaseMethods.getLinks().forEach(URLProtection.cache::put);
                }
            }, 5000, 5*60*1000L);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Logger.getLogger("ServerCount").info("[🍁] Atualmente protegendo " + jda.getGuilds().size() + " servidores");
                }
            }, 5000, 30*60*1000L);
        } catch(Exception e) {
            ExceptionManager.reportError(e);
        }
    }

}
