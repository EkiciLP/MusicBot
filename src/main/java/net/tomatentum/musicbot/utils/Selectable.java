package net.tomatentum.musicbot.utils;

import net.dv8tion.jda.api.entities.MessageReaction;

public interface Selectable {
	String getPage(int page);
	void handleReaction(MessageReaction reaction, int currentpage);
	int getPageSize(int Page);
	int getTotalPages();
}
