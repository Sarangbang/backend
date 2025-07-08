package sarangbang.site.global.notification;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import sarangbang.site.global.notification.dto.DiscordEmbed;
import sarangbang.site.global.notification.dto.DiscordMessage;
import sarangbang.site.global.notification.dto.EmbedField;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Profile("prod")
@RequiredArgsConstructor
public class DiscordWebhookService {

    @Value("${discord.webhook.url}")
    private String webhookUrl;

    private final WebClient.Builder webClientBuilder;

    public void sendExceptionNotification(Exception e, HttpServletRequest request) {
        log.info("Sending exception notification to Discord...");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        final DiscordMessage discordMessage = getDiscordMessage(e, request, sw);

        webClientBuilder.build()
            .post()
            .uri(webhookUrl)
            .bodyValue(discordMessage)
            .retrieve()
            .bodyToMono(Void.class)
            .doOnSuccess(v -> log.info("Successfully sent exception notification to Discord."))
            .doOnError(error -> log.error("Failed to send exception notification to Discord.", error))
            .subscribe();
    }

    private static DiscordMessage getDiscordMessage(Exception e, HttpServletRequest request, StringWriter sw) {
        String stackTrace = sw.toString();
        if (stackTrace.length() > 500) {
            stackTrace = stackTrace.substring(0, 250) + "\n...\n...\n" + stackTrace.substring(stackTrace.length() - 250);
        }
        
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            userAgent = "N/A";
        }

        List<EmbedField> fields = new ArrayList<>(List.of(
                new EmbedField("Request-URI", request.getRequestURI(), false),
                new EmbedField("Request-Method", request.getMethod(), false),
                new EmbedField("Client-IP", getClientIp(request), true),
                new EmbedField("User-Agent", userAgent, true),
                new EmbedField("Error-Message", e.getMessage() != null ? e.getMessage() : "N/A", false)
        ));
        fields.add(new EmbedField("Stack-Trace", "```" + stackTrace + "```", false));


        DiscordEmbed embed = new DiscordEmbed(
                "🚨 Exception Occurred!",
                null,
                Color.RED.getRGB() & 0xFFFFFF,
                fields
        );

        return new DiscordMessage("Unhandled Exception", List.of(embed));
    }

    private static String getClientIp(HttpServletRequest request) {
        String[] headerCandidates = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String header : headerCandidates) {
            String ipList = request.getHeader(header);
            if (ipList != null && ipList.length() != 0 && !"unknown".equalsIgnoreCase(ipList)) {
                return ipList.split(",")[0];
            }
        }

        return request.getRemoteAddr();
    }
} 