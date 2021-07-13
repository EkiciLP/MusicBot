package net.tomatentum.musicbot.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.ArrayList;
import java.util.List;

public class PageManager<T> {

	private List<T> items;
	private int pageSize;

	public PageManager(List<T> allItems, int pageSize) {
		this.items = allItems;
		this.pageSize = pageSize;
	}

	public List<T> getPage(int page) {
		if (page <= getTotalPages() && page > 0) {
			List<T> onPage = new ArrayList<>();
			page--;

			int lowerBound = page * pageSize;
			int upperBound = Math.min(lowerBound + pageSize, items.size());

			for (int i = lowerBound; i < upperBound; i++) {
				onPage.add(items.get(i));
			}

			return onPage;
		} else
			return new ArrayList<>();
	}

	public void addItem(T Item) {
		if (items.contains(Item)) {
			return;
		}
		items.add(Item);
	}

	public void removeItem(T Item) throws NullPointerException {


		if (items.contains(Item)) {
			items.remove(Item);
		} else {
			throw new NullPointerException("The Item does not exist");

		}
	}

	public int getTotalPages() {
		int totalPages = (int) Math.ceil((double) items.size() / pageSize);

		return totalPages;
	}

	public List<T> getContents() {
		return items;
	}
}
