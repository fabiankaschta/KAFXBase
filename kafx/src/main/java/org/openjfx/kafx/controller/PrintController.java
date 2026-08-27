package org.openjfx.kafx.controller;

import java.util.function.Function;

import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.PageRange;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrintResolution;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.transform.Scale;

public class PrintController extends Controller {

	public final static Function<Printer, PageLayout> A4_LANDSCAPE = printer -> printer.createPageLayout(Paper.A4,
			PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT);
	public final static Function<Printer, PageLayout> A4_PORTRAIT = printer -> printer.createPageLayout(Paper.A4,
			PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);

	private static PrintController controller;

	protected PrintController() {
	}

	public static void init() {
		init(new PrintController());
	}

	public static void init(PrintController controller) {
		LogController.log(LogController.DEBUG, "init print controller");
		PrintController.controller = controller;
	}

	public static boolean isInitialized() {
		return controller != null;
	}

	public static void showPrintSinglePreview(Node printable) {
		showPrintSinglePreview(printable, null);
	}

	public static void showPrintSinglePreview(Node printable, Function<Printer, PageLayout> pageLayout) {
		if (isInitialized()) {
			controller.createPrintPreviewDialog(printable).showAndWait().ifPresent(print -> {
				if (print) {
					printSinglePage(printable, pageLayout);
				}
			});
		}
	}

	public static void showPrintPreview(Node printable) {
		showPrintPreview(printable, null);
	}

	public static void showPrintPreview(Node printable, Function<Printer, PageLayout> pageLayout) {
		if (isInitialized()) {
			controller.createPrintPreviewDialog(printable).showAndWait().ifPresent(print -> {
				if (print) {
					print(printable, pageLayout);
				}
			});
		}
	}

	protected Dialog<Boolean> createPrintPreviewDialog(Node printable) {
		DialogPane root = new DialogPane();
		root.setContent(printable);
		root.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		root.getStylesheets().add(Controller.getStylesheetURL().toExternalForm());
		Dialog<Boolean> dialog = new Dialog<>();
		dialog.setTitle(TranslationController.translate("dialog_printPreview_title"));
		dialog.setDialogPane(root);
		dialog.setResultConverter(type -> type == ButtonType.OK);
		return dialog;
	}

	public static void print(Node printableNode) {
		print(printableNode, null);
	}

	public static void print(Node printableNode, Function<Printer, PageLayout> pageLayout) {
		if (isInitialized()) {
			LogController.log(LogController.DEBUG, "print started");
			PrinterJob job = PrinterJob.createPrinterJob();
			if (printableNode != null) {
				if (job != null && job.showPrintDialog(Controller.getPrimaryStage().getOwner())) {
					if (pageLayout != null) {
						job.getJobSettings().setPageLayout(pageLayout.apply(job.getPrinter()));
					}
					if (job.showPageSetupDialog(Controller.getPrimaryStage().getOwner())) {
						printJob(printableNode, job);
					} else {
						LogController.log(LogController.DEBUG, "print aborted - cancelled");
					}
				} else {
					LogController.log(LogController.DEBUG, "print aborted - cancelled");
				}
				printableNode.setManaged(true);
			} else {
				LogController.log(LogController.DEBUG, "print aborted - nothing to print");
			}
		}
	}

	public static void printSinglePage(Node printableNode) {
		printSinglePage(printableNode, null);
	}

	public static void printSinglePage(Node printableNode, Function<Printer, PageLayout> pageLayout) {
		if (isInitialized()) {
			LogController.log(LogController.DEBUG, "print started");
			PrinterJob job = PrinterJob.createPrinterJob();
			job.getJobSettings().setPageRanges(new PageRange(1, 1));
			if (printableNode != null) {
				if (job != null && job.showPrintDialog(Controller.getPrimaryStage().getOwner())) {
					if (pageLayout != null) {
						job.getJobSettings().setPageLayout(pageLayout.apply(job.getPrinter()));
					}
					if (job.showPageSetupDialog(Controller.getPrimaryStage().getOwner())) {
						Scale scale = scaleToPage(printableNode, job);
						printJob(printableNode, job);
						printableNode.getTransforms().remove(scale);
					} else {
						LogController.log(LogController.DEBUG, "print aborted - cancelled");
					}
				} else {
					LogController.log(LogController.DEBUG, "print aborted - cancelled");
				}
				printableNode.setManaged(true);
			} else {
				LogController.log(LogController.DEBUG, "print aborted - nothing to print");
			}
		}
	}

	private static Scale scaleToPage(Node node, PrinterJob job) {
		node.setManaged(false);
		PageLayout pageLayout = job.getJobSettings().getPageLayout();
		double width = node.prefWidth(-1);
		double height = node.prefHeight(-1);
		PrintResolution resolution = job.getJobSettings().getPrintResolution();
		width /= resolution.getFeedResolution();
		height /= resolution.getCrossFeedResolution();
		double scaleX = pageLayout.getPrintableWidth() / width / 600;
		double scaleY = pageLayout.getPrintableHeight() / height / 600;
		Scale scale = new Scale(Math.min(scaleX, scaleY), Math.min(scaleX, scaleY));
		node.getTransforms().add(scale);
		node.autosize();
		return scale;
	}

	private static void printJob(Node node, PrinterJob job) {
		boolean success = job.printPage(node);
		if (success) {
			job.endJob();
			LogController.log(LogController.DEBUG, "print successful");
		} else {
			LogController.log(LogController.DEBUG, "print unsuccessful");
			job.cancelJob();
		}
	}

}
