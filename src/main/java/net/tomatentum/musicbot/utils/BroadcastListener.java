package net.tomatentum.musicbot.utils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class BroadcastListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().equals(event.getJDA().getSelfUser()))
            return;

        if (event.getAuthor().getIdLong() == 365497290053386242L) {
            if (event.getMessage().getContentDisplay().startsWith("*broadcast")) {

                String[] args = event.getMessage().getContentDisplay().split(" ");

                if (args.length < 3) {
                    event.getMessage().reply("Please give at least 2 arguments!").queue();
                    return;
                }

                StringBuilder builder = new StringBuilder();

                for (int i = 2; i < args.length; i++) {
                    builder.append(args[i]).append(" ");
                }

                Utils.broadcastMessage(args[1], builder.toString());
                event.getMessage().reply("Broadcasted").queue();


            }

        }

    }
}
