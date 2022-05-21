package me.heyyczer.phishing.utils.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum BotConfig {

    URL_PROTECTION_REPORT("Canal Proteção Anti-Phishing", "urlProtectionChannel", "textchannel", null);

    @Getter
    private String name;
    @Getter
    private String configKey;
    @Getter
    private String type;
    @Getter
    private Object defaultValue;

    public static BotConfig fromConfigKey(String configKey) {
        for(BotConfig config : BotConfig.values())
            if(config.getConfigKey().equalsIgnoreCase(configKey))
                return config;
        return null;
    }

}
