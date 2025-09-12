package org.openjfx.kafx.controller;

import javafx.print.PageLayout;
import javafx.print.PageRange;
import javafx.print.PrintResolution;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.transform.Scale;

public abstract class PrintController extends Controller {

	private static PrintController controller;

	protected PrintController() {
	}

	public static void init(PrintController controller) {
		log(DEBUG, "init print controller");
		PrintController.controller = controller;
	}

	public static boolean isInitialized() {
		return controller != null;
	}

	public static void print() {
		if (isInitialized()) {
			log(DEBUG, "print started");
			PrinterJob job = PrinterJob.createPrinterJob();
			job.getJobSettings().setPageRanges(new PageRange(1, 1));
			Node root = controller.getPrintableNode();
			if (root != null) {
				if (job != null && job.showPrintDialog(root.getScene().getWindow())) {
					Printer printer = job.getPrinter();
					PageLayout pageLayout = controller.getDefaultPageLayout(printer);
					job.getJobSettings().setPageLayout(pageLayout);
					if (job.showPageSetupDialog(root.getScene().getWindow())) {
						root.setManaged(false);
						pageLayout = job.getJobSettings().getPageLayout();

						double width = root.getLayoutBounds().getWidth();
						double height = root.getLayoutBounds().getHeight();
						PrintResolution resolution = job.getJobSettings().getPrintResolution();
						width /= resolution.getFeedResolution();
						height /= resolution.getCrossFeedResolution();
						double scaleX = pageLayout.getPrintableWidth() / width / 600;
						double scaleY = pageLayout.getPrintableHeight() / height / 600;
						Scale scale = new Scale(Math.min(scaleX, scaleY), Math.min(scaleX, scaleY));
						root.getTransforms().add(scale);

						boolean success = job.printPage(root);
						if (success) {
							job.endJob();
							log(DEBUG, "print successful");
						} else {
							log(DEBUG, "print unsuccessful");
							job.cancelJob();
						}
						root.getTransforms().remove(scale);
					} else {
						log(DEBUG, "print aborted - cancelled");
					}
				} else {
					log(DEBUG, "print aborted - cancelled");
				}
				root.setManaged(true);
			} else {
				log(DEBUG, "print aborted - nothing to print");
			}
		}
	}

	protected PageLayout getDefaultPageLayout(Printer printer) {
		return printer.getDefaultPageLayout();
	}

	protected abstract Node getPrintableNode();

}
