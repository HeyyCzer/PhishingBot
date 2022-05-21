package me.heyyczer.phishing.utils.lib.commands;

import me.heyyczer.phishing.Main;
import me.heyyczer.phishing.commands.ConfigCmd;
import me.heyyczer.phishing.commands.StatsCmd;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class CommandManager {

    private static final List<ICommand> commands = new ArrayList<>();

    public CommandManager() {
        addCommand(new StatsCmd());
        addCommand(new ConfigCmd());

        buildCommands();
    }

    private void addCommand(ICommand cmd) {
        boolean nameFound = commands.stream().anyMatch(it -> it.getName().equalsIgnoreCase(cmd.getName()));
        if (nameFound)
            throw new IllegalArgumentException("Um comando com o mesmo nome já existe!");
        commands.add(cmd);
    }

    private static void buildCommands() {
        List<CommandData> commandList = new ArrayList<>();
        for (ICommand cmd : commands)
            commandList.add(new CommandData(cmd.getName(), cmd.getDescription()).addOptions(cmd.getArguments()));
        Main.jda.updateCommands().addCommands(commandList).queue();
    }

    @Nullable
    private ICommand getCommand(String commandName) {
        String search = commandName.toLowerCase();
        for (ICommand cmd : commands) {
            if (cmd.getName().equals(search))
                return cmd;
        }
        return null;
    }

    public void handle(SlashCommandEvent event) {
        ICommand cmd = this.getCommand(event.getName());
        if (cmd != null) {
            if (cmd.getPermission() != null && !event.getMember().hasPermission(cmd.getPermission())) {
                Logger.getLogger("CommandManager").info("[✖️] Usuário " + event.getUser().getAsTag() + " tentou executar um comando sem ter permissão (/" + cmd.getName() + " - " + cmd.getPermission().getName() + ").");

                event.deferReply().queue();
                event.getHook().editOriginal("**Erro 403 »** Você não tem permissão para executar este comando.").queue(msg ->
                        msg.delete().queueAfter(10, TimeUnit.SECONDS),
                        Main.errorHandler
                );
                return;
            }

            Logger.getLogger("CommandManager").info("[✔️] Usuário " + event.getUser().getAsTag() + " executou um comando (/" + cmd.getName() + " - " + cmd.getPermission().getName() + ").");

            event.deferReply(cmd.isEphemeral()).queue();
            cmd.handle(event);
        }
    }

}
