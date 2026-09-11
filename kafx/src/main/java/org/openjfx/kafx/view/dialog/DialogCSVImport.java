package org.openjfx.kafx.view.dialog;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.openjfx.kafx.controller.ExceptionController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.io.CSVParser;
import org.openjfx.kafx.view.dialog.userinput.UserInputCheckBox;
import org.openjfx.kafx.view.dialog.userinput.UserInputChoiceBox;
import org.openjfx.kafx.view.tableview.TableViewFullSize;

import javafx.collections.ListChangeListener;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.util.StringConverter;

public class DialogCSVImport<T> extends DialogUserInput<Boolean> {

	private final DialogPaneCustom dialogPane;
	private final UserInputChoiceBox<Character> separator;
	private final UserInputChoiceBox<Character> quotationMark;
	private final UserInputCheckBox labeled;
	private final TableViewFullSize<T> preview;
	private final int previewAmount;

	public DialogCSVImport(CSVParser<T> parser, TableViewFullSize<T> preview, int previewAmount, Consumer<T> importer) {
		this(TranslationController.translate("dialog_import_title"), parser, preview, previewAmount, importer);
	}

	public DialogCSVImport(String title, CSVParser<T> parser, TableViewFullSize<T> preview, int previewAmount,
			Consumer<T> importer) {
		super(title, new DialogPaneCustom());

		this.dialogPane = (DialogPaneCustom) this.getDialogPane();
		this.preview = preview;
		this.previewAmount = previewAmount;

		ChoiceBox<Character> separatorChoiceBox = new ChoiceBox<>();
		for (char s : CSVParser.defaultSeparators()) {
			separatorChoiceBox.getItems().add(s);
		}
		separatorChoiceBox.setConverter(new StringConverter<Character>() {
			@Override
			public String toString(Character object) {
				switch (object) {
				case ',':
					return TranslationController.translate("key_comma");
				case ';':
					return TranslationController.translate("key_semicolon");
				case ' ':
					return TranslationController.translate("key_space");
				case '\t':
					return TranslationController.translate("key_tab");
				case ':':
					return TranslationController.translate("key_colon");
				default:
					return String.valueOf(object);
				}
			}

			@Override
			public Character fromString(String string) {
				throw new UnsupportedOperationException();
			}
		});
		this.separator = new UserInputChoiceBox<Character>(separatorChoiceBox);
		try {
			parser.guessSeparator();
		} catch (IOException e) {
			ExceptionController.exception(e);
		}
		this.separator.setDefaultValue(parser.getSeparator());
		this.separator.selectDefault();
		this.separator.valueProperty().subscribe(v -> {
			parser.setSeparator(v);
			try {
				preview.getItems().setAll(parser.preview(this.previewAmount));
			} catch (IOException e) {
				ExceptionController.exception(e);
			}
		});
		super.addInput(this.separator, TranslationController.translate("dialog_import_separator"));

		ChoiceBox<Character> quotationMarkChoiceBox = new ChoiceBox<>();
		quotationMarkChoiceBox.getItems().addAll('"', '\'');
		quotationMarkChoiceBox.setConverter(new StringConverter<Character>() {
			@Override
			public String toString(Character object) {
				switch (object) {
				case '"':
					return TranslationController.translate("csv_quotationMark_double");
				case '\'':
					return TranslationController.translate("csv_quotationMark_single");
				default:
					return String.valueOf(object);
				}
			}

			@Override
			public Character fromString(String string) {
				throw new UnsupportedOperationException();
			}
		});
		this.quotationMark = new UserInputChoiceBox<Character>(quotationMarkChoiceBox, '"');
		this.quotationMark.valueProperty().subscribe(v -> {
			parser.setQuotationMark(v);
			try {
				preview.getItems().setAll(parser.preview(previewAmount));
			} catch (IOException e) {
				ExceptionController.exception(e);
			}
		});
		super.addInput(this.quotationMark, TranslationController.translate("dialog_import_quotationMark"));

		this.labeled = new UserInputCheckBox(new CheckBox());
		try {
			parser.checkContainsLabels(preview.getColumns().stream().map(c -> c.getText()).toArray(n -> new String[n]));
		} catch (IOException e) {
			ExceptionController.exception(e);
		}
		this.labeled.setDefaultValue(parser.containsLabel());
		this.labeled.selectDefault();
		this.labeled.valueProperty().subscribe(v -> {
			parser.setContainsLabel(v);
			try {
				preview.getItems().setAll(parser.preview(previewAmount));
			} catch (IOException e) {
				ExceptionController.exception(e);
			}
		});
		super.addInput(this.labeled, TranslationController.translate("dialog_import_labeled"));

		this.dialogPane.setExpandableContent(this.preview);
		this.preview.getColumns().addListener((ListChangeListener<TableColumn<T, ?>>) _ -> {
			try {
				preview.getItems().setAll(parser.preview(previewAmount));
			} catch (IOException e) {
				ExceptionController.exception(e);
			}
		});
		this.dialogPane.expandedProperty().subscribe(() -> this.setResizable(false));
		this.dialogPane.setExpanded(true);

		this.dialogPane.getButtonTypes().add(IMPORT);
		this.dialogPane.setDetailsButtonMoreText(TranslationController.translate("dialog_import_preview_show"));
		this.dialogPane.setDetailsButtonLessText(TranslationController.translate("dialog_import_preview_hide"));

		this.setResultConverter(r -> {
			if (r == IMPORT) {
				try {
					Stream<T> stream = parser.map();
					stream.forEach(t -> importer.accept(t));
					stream.close();
					return true;
				} catch (IOException e) {
					ExceptionController.exception(e);
					return false;
				}
			} else {
				return false;
			}
		});
	}

}
