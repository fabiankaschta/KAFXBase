module org.openjfx.kafx {
    requires transitive javafx.controls;
	requires transitive java.logging;
	requires transitive org.pf4j;
	exports org.openjfx.kafx.controller;
	exports org.openjfx.kafx.io;
	exports org.openjfx.kafx.lang;
	exports org.openjfx.kafx.view.alert;
	exports org.openjfx.kafx.view.color;
	exports org.openjfx.kafx.view.control;
	exports org.openjfx.kafx.view.converter;
	exports org.openjfx.kafx.view.dialog;
	exports org.openjfx.kafx.view.dialog.userinput;
	exports org.openjfx.kafx.view.listview;
	exports org.openjfx.kafx.view.menu;
	exports org.openjfx.kafx.view.pane;
	exports org.openjfx.kafx.view.tableview;
	exports org.openjfx.kafx.view.treeview;
}
