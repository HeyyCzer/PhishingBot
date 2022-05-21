package me.heyyczer.phishing.utils;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import me.heyyczer.phishing.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.awt.*;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ExceptionManager {

	private static WebhookClient webhookClient = null;

	private static WebhookClient getWebhookClient() {
		if(webhookClient == null) {
			WebhookClientBuilder builder = new WebhookClientBuilder("https://canary.discord.com/api/webhooks/869069174377426954/3aIqdauMoi0wSskQaNmuMWSue32XJyc72uTpuWBxAoWI0sdvgq7pt-H9013gWBw-uNxV");
			builder.setThreadFactory(job -> {
				Thread thread = new Thread(job);
				thread.setName("PhishingBlocker | Error Reporting");
				thread.setDaemon(true);
				return thread;
			});
			builder.setWait(true);
			webhookClient = builder.build();
		}
		return webhookClient;
	}

	public static void reportError(Throwable e) {
		try {
			e.printStackTrace();

			String message = "**Local:** " + e.getStackTrace()[0].toString() + "\n**Tipo:** " + e + "\n**Mensagem:** " + e.getMessage();
			reportError(message);
		}catch(Exception ex) {
			Logger.getLogger("ExceptionHandler").severe("Erro ao reportar ao Discord");
			ex.printStackTrace();
		}
	}

	public static void reportError(String message) {
		try {
			WebhookClient client = getWebhookClient();

			if(message != null && !message.equals("")) {
				if(message.length() >= 999) {
					message = message.substring(0, 999);
					message = message.concat("\n\n**e mais...**");
				}
			}else
				message = "Um erro desconhecido ocorreu! Verifique o console da aplicação.";

			WebhookEmbed embed = new WebhookEmbedBuilder()
					.setColor(0xFF0000)
					.setDescription(message)
					.setAuthor(new WebhookEmbed.EmbedAuthor("Ocorreu um erro na aplicação", null, Main.jda.getSelfUser().getEffectiveAvatarUrl()))
					.setTimestamp(Instant.now())
					.build();

			WebhookMessageBuilder builder = new WebhookMessageBuilder();
			builder.setUsername(Main.jda.getSelfUser().getName() + " | Error Reporting");
			builder.addEmbeds(embed);
			client.send(builder.build());
		}catch(Exception ex) {
			Logger.getLogger("ExceptionHandler").severe("Erro ao reportar ao Discord (2)");
			ex.printStackTrace();
		}
	}

}
