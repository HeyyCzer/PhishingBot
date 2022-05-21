package me.heyyczer.phishing.utils.config;

import net.dv8tion.jda.api.entities.Guild;

public class ConfigValidator {

    private ConfigValidator() {

    }

    public static Object validate(Guild g, BotConfig config, String value) {
        if(g == null && (config.getType().equals("textchannel") || config.getType().equals("role") || config.getType().equals("emoji"))) {
            throw new IllegalArgumentException("Argumentos faltando ao verificar a configuração \"" + config.getConfigKey() + "\" do tipo \"" + config.getType() + "\"");
        }

        if(g != null && config.getType().equals("textchannel") && g.getTextChannelById(value) != null)
            return g.getTextChannelById(value);
        if(g != null && config.getType().equals("role") && g.getRoleById(value) != null)
            return g.getRoleById(value);
        if(g != null && config.getType().equals("emoji") && g.getEmoteById(value) != null)
            return g.getEmoteById(value);

        if(value.matches(config.getType()))
            return value;

        return null;
    }
    public static String formatValue(Guild g, BotConfig config, String value) {
        if(value.equals("null")) {
            return "Nulo";
        }

        if(config.getType().equals("textchannel") && g.getTextChannelById(value) != null)
            return g.getTextChannelById(value).getAsMention();
        if(config.getType().equals("role") && g.getRoleById(value) != null)
            return g.getRoleById(value).getAsMention();
        if(config.getType().equals("emoji") && g.getEmoteById(value) != null)
            return g.getEmoteById(value).getAsMention();

        if(value.matches(config.getType()))
            return value;

        return null;
    }

}
