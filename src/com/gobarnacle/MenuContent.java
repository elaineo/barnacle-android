package com.gobarnacle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuContent {
	public static List<MenuItem> ITEMS = new ArrayList<MenuItem>();
	/**
	 * A map of items, by ID.
	 */
	public static Map<String, MenuItem> ITEM_MAP = new HashMap<String, MenuItem>();

	static {
		addItem(new MenuItem("1", "Search"));
		addItem(new MenuItem("2", "Add Route"));
		addItem(new MenuItem("3", "Account"));
		addItem(new MenuItem("3", "Settings"));
	}

	private static void addItem(MenuItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	public static class MenuItem {
		public String id;
		public String content;

		public MenuItem(String id, String content) {
			this.id = id;
			this.content = content;
		}

		@Override
		public String toString() {
			return content;
		}
	}
}
