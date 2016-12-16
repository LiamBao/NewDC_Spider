package com.cic.datacrawl.ui.tools;

import java.awt.event.KeyEvent;

public class CommandConstants {

	public static final ItemDesc NEW = new ItemDesc("New", 'C', KeyEvent.VK_N,
			"New", "Create a new script file.", "doc_text_image.png");

	public static final ItemDesc OPEN = new ItemDesc("Open...", 'O',
			KeyEvent.VK_O, "Open", "Open an exists script file.", "folder.png");

	public static final ItemDesc REOPEN = new ItemDesc("Reopen", (char) 0, 0,
			"Reopen", "", "arrow_refresh.png");

	public static final ItemDesc SAVE = new ItemDesc("Save", 'S',
			KeyEvent.VK_S, "Save", "Save", "save.png");

	public static final ItemDesc SAVE_AS = new ItemDesc("Save as...", (char) 0,
			0, "Save as...", "", "");

	public static final ItemDesc SAVE_ALL = new ItemDesc("Save All", (char) 0,
			0, "Save All", "Save All", "");

	public static final ItemDesc COMPLIE = new ItemDesc("Complie Check",
			(char) 0, 0, "Complie Check",
			"Check complie error in script which is opened in current window.",
			"spellcheck.png");

	public static final ItemDesc RUN = new ItemDesc("Run...", 'R',
			KeyEvent.VK_R, "Load", "Run...", "run.png");

	public static final ItemDesc EXIT = new ItemDesc("Exit", 'Q',
			KeyEvent.VK_Q, "Exit", "Exit", "quit.png");

	public static final ItemDesc SELECT_ALL = new ItemDesc("Select All", 'S',
			0, "Select All", "Select All Text in Output Console",
			"text_select_all.png");

	public static final ItemDesc CLEAR = new ItemDesc("Clear", 'C', 0,
			"Clear Output Console", "Clear Output Console", "ticket.png");

	public static final ItemDesc CODE_FORMAT = new ItemDesc(
			"Source Code Format", (char) 0, KeyEvent.VK_F,
			"Source Code Format", "Source Code Format (Ctrl + Shift + F)", "");

	public static final ItemDesc SEARCH_AND_REPLACE = new ItemDesc(
			"Search and Replace", (char) 0, KeyEvent.VK_H,
			"Search And Replace", "Search And Replace (Ctrl + H)",
			"Replace.png");

	public static final ItemDesc SEARCH = new ItemDesc("Search", (char) 0,
			KeyEvent.VK_F, "Search", "Search (Ctrl + F)", "Search.png");
	public static final ItemDesc SEARCH_NEXT = new ItemDesc("Search Next",
			(char) 0, KeyEvent.VK_F3, "Search Next", "Search Next (F3)",
			"Search_Next.png");
	public static final ItemDesc SEARCH_PREV = new ItemDesc("Search Prev",
			(char) 0, KeyEvent.VK_F3, "Search Prev",
			"Search Prev (Shift + F3)", "Search_Prev.png");
	public static final ItemDesc HIGHLIGHT = new ItemDesc(
			"Search And Highlight", (char) 0, KeyEvent.VK_F3,
			"Search And Highlight", "Search And Highlight (Ctrl + F3)",
			"Search And Highlighter.png");
	public static final ItemDesc REMOVE_ALL_HIGHLIGHT = new ItemDesc(
			"Remove All Highlight", (char) 0, KeyEvent.VK_F3,
			"Remove All Highlight", "Remove All Highlight (Alt + F3)",
			"wand.png");

	public static final ItemDesc UNLOCK = new ItemDesc("Auto Scroll", 'U', 0,
			"Autoscroll Output Console", "Autoscroll Output Console",
			"lock_unlock.png", ItemDesc.DEFAULT_DISABLE_VISIBLE);

	public static final ItemDesc LOCK = new ItemDesc("Lock", 'L', 0,
			"Lock Output Console", "Lock Output Console", "lock.png");

	public static final ItemDesc SCROLL_TO_SELECTION_START = new ItemDesc(
			"Scroll To Selection", (char) 0, 0, "Scroll To Selection",
			"Scroll To Selection", null);

	public static final ItemDesc NO_WRAP = new ItemDesc("No Wrap", 'N', 0,
			"No Wrap", "No Wrap", "LineNoWrap.png",
			ItemDesc.DEFAULT_DISABLE_VISIBLE);

	public static final ItemDesc AUTO_WRAP = new ItemDesc("Auto Wrap", 'A', 0,
			"Auto Wrap", "Auto Wrap", "LineAutoWrap.png");

	public static final ItemDesc UNDO = new ItemDesc("Undo", 'Z', 0, "Undo",
			"Undo", "arrow_undo.png");

	public static final ItemDesc REDO = new ItemDesc("Redo", 'Y', 0, "Redo",
			"Redo", "arrow_redo.png");

	public static final ItemDesc CUT = new ItemDesc("Cut", 'X', 0, "Cut",
			"Cut", "page_cut.png");

	public static final ItemDesc COPY = new ItemDesc("Copy", 'C', 0, "Copy",
			"Copy", "page_copy.png");
	
	public static final ItemDesc COPY_SHOW_TEXT = new ItemDesc(
			"Copy showable text", 'C', 0, "Copy showable text",
			"Copy showable text", "page_copy.png");
	
	public static final ItemDesc COPY_TEXT = new ItemDesc("Copy Text", 'C', 0,
			"Copy Text", "Copy Text", "page_copy.png");
	public static final ItemDesc COPY_TEXT_CURRENT_NODE = new ItemDesc("Copy Text in Current Node", 'C', 0,
	                                          			"Copy Text in Current Node", "Copy Text in Current Node", "page_copy.png");
	public static final ItemDesc COPY_XML = new ItemDesc("Copy Xml", 'C', 0,
			"Copy Xml", "Copy Xml", "page_copy.png");

	public static final ItemDesc PASTE = new ItemDesc("Paste", 'V', 0, "Paste",
			"Paste", "page_paste.png");

	public static final ItemDesc EXPORT = new ItemDesc("Export", (char) 0, 0,
			"Export", "Export All", null);

	public static final ItemDesc EXPORT_ALL = new ItemDesc("Export All",
			(char) 0, 0, "Export All", "Export All", null);

	public static final ItemDesc SEARCH_FUNCTION = new ItemDesc(
			"Search function...", 'F', KeyEvent.VK_L, "Search function...",
			"Search function...(Ctrl + L)", "find.png");

	public static final ItemDesc CHOOSE_COLOR = new ItemDesc("Choose Color...",
			(char) 0, 0, "Choose Color...", "Choose Color...",
			"color_swatch_2.png");

	public static final ItemDesc EXECUTE = new ItemDesc("Execute", 'G',
			KeyEvent.VK_F5, "Execute", "Execute (F5)", "exec.png");

	public static final ItemDesc DEBUG_BREAK = new ItemDesc("Break", 'B',
			KeyEvent.VK_PAUSE, "Break", "Break (Pause)", "debug_stop.png",
			ItemDesc.DEFAULT_ENABLE_VISIBLE);

	public static final ItemDesc DEBUG_RUN = new ItemDesc("Continue", 'G',
			KeyEvent.VK_F4, "Continue", "Continue (F4)", "debug_run.png",
			ItemDesc.DEFAULT_DISABLE_VISIBLE);

	public static final ItemDesc DEBUG_RUN_SELECTED = new ItemDesc(
			"Execute Selection", (char) 0, 0, "Execute Selection",
			"Execute codes which were selected in current window.",
			"debug_run.png", ItemDesc.DEFAULT_DISABLE_VISIBLE);

	public static final ItemDesc DEBUG_STEP_INTO = new ItemDesc("Step Into",
			'I', KeyEvent.VK_F7, "Step Into", "Step Into (F7)",
			"debug_step_into.PNG", ItemDesc.DEFAULT_DISABLE_VISIBLE);

	public static final ItemDesc DEBUG_STEP_OVER = new ItemDesc("Step Over",
			'O', KeyEvent.VK_F9, "Step Over", "Step Over (F9)",
			"debug_step_over.PNG", ItemDesc.DEFAULT_DISABLE_VISIBLE);

	public static final ItemDesc DEBUG_STEP_OUT = new ItemDesc("Step Out", 'T',
			KeyEvent.VK_F11, "Step Out", "Step Out (F11)",
			"debug_step_out.PNG", ItemDesc.DEFAULT_DISABLE_VISIBLE);

	public static final ItemDesc PLAF_METAL = new ItemDesc("Metal", 'M', 0,
			"Metal", "Metal", "");

	public static final ItemDesc PLAF_WINDOWS = new ItemDesc("Windows", 'W', 0,
			"Windows", "Windows", "");

	public static final ItemDesc PLAF_MOTIF = new ItemDesc("Motif", 'F', 0,
			"Motif", "Motif", "");

	public static final ItemDesc CONSOLE = new ItemDesc("Console", 'C', 0,
			"Console", "Console", "application_view_xp_terminal.png");

	public static final ItemDesc CASCADE = new ItemDesc("Cascade", 'A', 0,
			"Cascade", "Cascade", "application_cascade.png");

	public static final ItemDesc TILE = new ItemDesc("Tile", 'T', 0, "Tile",
			"Tile", "application_tile_vertical.png");

	public static final ItemDesc SET_BREAKPOINT = new ItemDesc(
			"Set Breakpoint", (char) 0, 0, "Set Breakpoint", "Set Breakpoint",
			"");
	public static final ItemDesc CLEAR_ALL_BREAKPOINT = new ItemDesc(
			"Clear All Breakpoint", (char) 0, 0, "Clear All Breakpoint",
			"Clear All Breakpoint", "");
	public static final ItemDesc CLEAR_BREAKPOINT = new ItemDesc(
			"Clear Breakpoint", (char) 0, 0, "Clear Breakpoint",
			"Clear Breakpoint", "");
	public static final ItemDesc JUMP_TO = new ItemDesc("Jump to", (char) 0, 0,
			"Jump to", "Jump to", "", ItemDesc.DEFAULT_DISABLE_VISIBLE);

	public static final ItemDesc SHOW_IN_BROWSER = new ItemDesc(
			"Show in Browser", (char) 0, 0, "Show in Browser",
			"Show in Browser", "", ItemDesc.DEFAULT_DISABLE_VISIBLE);

	public static final ItemDesc SCROLL_TO_SELECTION = new ItemDesc(
			"Scroll to Selection", (char) 0, 0, "Scroll to Selection",
			"Scroll to Selection", "", ItemDesc.DEFAULT_ENABLE_VISIBLE);

	public static final ItemDesc WATCH = new ItemDesc("Watch", (char) 0, 0,
			"Watch", "Watch", "", ItemDesc.DEFAULT_DISABLE_VISIBLE);
	public static final ItemDesc DISPLAY = new ItemDesc("Display", (char) 0, 0,
			"Display", "Display", "", ItemDesc.DEFAULT_DISABLE_VISIBLE);

	public static final ItemDesc REMOVE = new ItemDesc("Remove", (char) 0, 0,
			"Remove", "Remove", "", ItemDesc.DEFAULT_DISABLE_VISIBLE);
	public static final ItemDesc REMOVE_ALL = new ItemDesc("Remove All",
			(char) 0, 0, "Remove All", "Remove All", "",
			ItemDesc.DEFAULT_DISABLE_VISIBLE);

	public static final ItemDesc ADD_NEW_WATCH = new ItemDesc("Add New Watch",
			(char) 0, 0, "Add New Watch", "Add New Watch", "",
			ItemDesc.DEFAULT_DISABLE_INVISIBLE);

	public static final ItemDesc MORE_WINDOWS = new ItemDesc("More Windows...",
			'M', KeyEvent.VK_W, "More Windows...", "More Windows...", "");

	public static final ItemDesc LOCATE_BROWSER_ELEMENT = new ItemDesc(
			"Locate Element", 'L', 0, "Locate Element",
			"Locate element in browser", "target.png");

	public static final ItemDesc[] GROUP_MENU_FILE = { NEW, OPEN, REOPEN, SAVE,
			SAVE_AS, SAVE_ALL, null, COMPLIE, RUN, null, EXIT };

	public static final ItemDesc[] GROUP_MENU_EDIT = { UNDO, REDO, null, CUT,
			COPY, PASTE, null, CODE_FORMAT, null, SEARCH, SEARCH_AND_REPLACE,
			null, SEARCH_PREV, SEARCH_NEXT, null, HIGHLIGHT,
			REMOVE_ALL_HIGHLIGHT, null, SEARCH_FUNCTION, null, CHOOSE_COLOR };

	public static final ItemDesc[] GROUP_MENU_EXPORT = { EXPORT, EXPORT_ALL };

	public static final ItemDesc[] GROUP_MENU_DEBUG = { EXECUTE, null,// DEBUG_BREAK,
			DEBUG_RUN, DEBUG_STEP_INTO, DEBUG_STEP_OVER, DEBUG_STEP_OUT };

	public static final ItemDesc[] GROUP_MENU_WINDOW = { CASCADE, TILE };

	public static final ItemDesc[] GROUP_MENU_PLAF = { PLAF_METAL,
			PLAF_WINDOWS, PLAF_MOTIF };

	public static final ItemDesc[] GROUP_MENU_FILE_WINDOW_POPUP = { SELECT_ALL,
			null, CUT, COPY, PASTE, null, JUMP_TO, null, SET_BREAKPOINT,
			CLEAR_BREAKPOINT, CLEAR_ALL_BREAKPOINT, null, WATCH, DISPLAY, null,
			SHOW_IN_BROWSER, null, LOCATE_BROWSER_ELEMENT, DEBUG_RUN_SELECTED };

	public static final ItemDesc[] GROUP_TOOLBAR_WINDOW = { CASCADE, TILE,
			null, CONSOLE };

	public static final ItemDesc[] GROUP_TOOLBAR_FILE = { NEW, OPEN, REOPEN,
			SAVE, COMPLIE };

	public static final ItemDesc[] GROUP_TOOLBAR_EDIT = { UNDO, REDO, null,
			CUT, COPY, PASTE, null, /*
									 * SEARCH_FUNCTION,
									 */
			LOCATE_BROWSER_ELEMENT, null, CHOOSE_COLOR };

	public static final ItemDesc[] GROUP_TOOLBAR_DEBUG = { EXECUTE, null,
			DEBUG_BREAK, DEBUG_RUN, DEBUG_STEP_INTO, DEBUG_STEP_OVER,
			DEBUG_STEP_OUT };

	public static final ItemDesc[] GROUP_MENU_CONSOLE_POPUP = { COPY, PASTE };
	public static final ItemDesc[] GROUP_MENU_EVALUATOR_POPUP = { REMOVE,
			REMOVE_ALL, ADD_NEW_WATCH };

	public static final ItemDesc[] GROUP_MENU_HTML_SOURCE_POPUP = { SELECT_ALL,
			COPY };

	public static final ItemDesc[] GROUP_MENU_OUTPUT_CONSOLE_POPUP = {
			SELECT_ALL, COPY, null, SCROLL_TO_SELECTION_START, null, CLEAR };

	public static final ItemDesc[] GROUP_TOOLBAR_OUTPUT_CONSOLE = { SELECT_ALL,
			COPY, null, AUTO_WRAP, NO_WRAP, null, LOCK, UNLOCK, null, CLEAR };

	public static final ItemDesc[] GROUP_TOOLBAR_SEARCH = { /*
															 * SEARCH,
															 * SEARCH_AND_REPLACE
															 * , null,
															 */SEARCH_PREV,
			SEARCH_NEXT, null, HIGHLIGHT, REMOVE_ALL_HIGHLIGHT, null,
			SEARCH_FUNCTION };

}
