package net.tomatentum.musicbot.utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public interface Selectable {
	String getPage(int page);
	void handleReaction(int item, int currentpage, Member member, ButtonClickEvent action);
	int getPageSize(int Page);
	int getTotalPages();
}
