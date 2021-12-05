package net.tomatentum.musicbot.utils;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.tomatentum.musicbot.TomatenMusic;
import org.jetbrains.annotations.NotNull;

public class BroadcastListener extends ListenerAdapter {

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        User tueem = TomatenMusic.getInstance().getBot().getUserById(365497290053386242L);

        if (event.getAuthor().getIdLong() == tueem.getIdLong()) {
            if (event.getMessage().getContentDisplay().startsWith("!broadcast")) {

                String[] args = event.getMessage().getContentDisplay().split(" ");

                if (args.length < 3) {
                    return;
                }

                StringBuilder builder = new StringBuilder();

                for (int i = 2; i < args.length; i++) {
                    builder.append(args[i]).append(" ");
                }

                Utils.broadcastMessage(args[1], builder.toString());

            }
        }
    }
}
