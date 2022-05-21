package me.heyyczer.phishing.commands;

import me.heyyczer.phishing.Main;
import me.heyyczer.phishing.database.DatabaseMethods;
import me.heyyczer.phishing.utils.config.BotConfig;
import me.heyyczer.phishing.utils.config.ConfigValidator;
import me.heyyczer.phishing.utils.lib.commands.ICommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigCmd implements ICommand {

    @Override
    public void handle(SlashCommandEvent event) {
        String action = event.getOption("ação").getAsString();
        String configKey = event.getOption("configuração").getAsString();
        OptionMapping valueMap = event.getOption("valor");

        if(action.equals("set") && valueMap == null) {
            event.getHook().editOriginal("Uso incorreto, você precisa definir um novo valor a configuração!").queue(null, Main.errorHandler);
            return;
        }

        BotConfig config = BotConfig.fromConfigKey(configKey);
        if(config == null) {
            event.getHook().editOriginal("A configuração selecionada não é válida.").queue(null, Main.errorHandler);
            return;
        }

        if(action.equals("set")) {
            String value = valueMap.getAsString();
            Object validation = ConfigValidator.validate(event.getGuild(), config, value);
            if(validation == null) {
                event.getHook().editOriginal("O valor inserido não é válido!").queue(null, Main.errorHandler);
                return;
            }

            DatabaseMethods.setConfig(event.getGuild().getId(), configKey, value);
            event.getHook().editOriginal("A configuração **" + configKey + "** foi definida com sucesso para **" + ConfigValidator.formatValue(event.getGuild(), config, value) + "**").queue(null, Main.errorHandler);
            return;
        }

        if(action.equals("check")) {
            Object configVal = DatabaseMethods.getConfig(event.getGuild().getId(), configKey);
            if(configVal == null) {
                event.getHook().editOriginal("A configuração **" + configKey + "** ainda não foi definida.").queue(null, Main.errorHandler);
                return;
            }

            event.getHook().editOriginal(
                    "A configuração **" + configKey + "** tem o valor de **" +
                            ConfigValidator.formatValue(event.getGuild(), config, configVal.toString()) +
                            "**").queue(null, Main.errorHandler);
        }
    }

    @Override
    public String getName() {
        return "config";
    }

    @Override
    public String getDescription() {
        return "🍁 | Configurar algo no BOT";
    }

    @Override
    public List<OptionData> getArguments() {
        List<Command.Choice> configs = new ArrayList<>();
        for(BotConfig b : BotConfig.values())
            configs.add(new Command.Choice(b.getName(), b.getConfigKey()));

        return Arrays.asList(
                new OptionData(OptionType.STRING, "ação", "Definir opção ou verificar uma configuração", true)
                        .addChoice("Ver valor atual", "check")
                        .addChoice("Definir novo valor", "set"),
                new OptionData(OptionType.STRING, "configuração", "Configuração a ser visualizada/alterada", true)
                        .addChoices(configs),
                new OptionData(OptionType.STRING, "valor", "Novo valor a ser definido na configuração selecionado")
        );
    }

    @Override
    public Permission getPermission() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public boolean isEphemeral() {
        return true;
    }

}
