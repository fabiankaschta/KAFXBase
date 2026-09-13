/**
 * Copyright (c) 2013, 2026, ControlsFX
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of ControlsFX, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

// UNMODIFIED COPY
package org.openjfx.kafx.view.skin;

import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openjfx.kafx.view.tableview.TableView3;

public class SouthTableHeaderRow3 extends Region {

    public static final String SOUTH_HEADER_STYLE = "south";

    private final TableView3Skin<?> skin;
    private final TableView3<?> control;
    private ObservableList<SouthTableColumnHeader3> southColumnHeaders;
    private final Map<TableColumnBase<?, ?>, SouthTableColumnHeader3> southColumnHeadersByColumn = new IdentityHashMap<>();

    private SouthTableHeaderRow3 parentSouthHeader;

    public SouthTableHeaderRow3(TableView3Skin<?> skin) {
        this.skin = skin;
        control = (TableView3<?>) skin.getSkinnable();
        getStyleClass().setAll("column-header-background", "south-header");
        init();
    }

    private void init() {
        updateSouthHeaders();
        control.getVisibleLeafColumns().addListener(weakVisibleLeafColumnsListener);
        control.southHeaderBlendedProperty().addListener(weakSouthHeaderBlendedListener);
        updateSouthHeaderRowStyle();

        if (control.getParent() != null && control.getParent() instanceof RowHeader3) {
            parentSouthHeader = ((TableView3Skin<?>) ((RowHeader3<?>) control.getParent()).getParentTableView().getSkin()).getSouthHeader();
        }
    }

    /***************************************************************************
     * Listeners                                                               *
     **************************************************************************/

    private final ListChangeListener<TableColumn<?, ?>> visibleLeafColumnsListener =
            c -> updateSouthHeaders();
    private final WeakListChangeListener<TableColumn<?, ?>> weakVisibleLeafColumnsListener =
            new WeakListChangeListener<>(visibleLeafColumnsListener);

    private final InvalidationListener southHeaderBlendedListener = o -> updateSouthHeaderRowStyle();
    private final WeakInvalidationListener weakSouthHeaderBlendedListener =
            new WeakInvalidationListener(southHeaderBlendedListener);

    /***************************************************************************
     * Public methods                                                         *
     **************************************************************************/

    /**
     * @return ObservableList
     */
    public ObservableList<SouthTableColumnHeader3> getSouthColumnHeaders() {
        if (southColumnHeaders == null) {
            southColumnHeaders = FXCollections.observableArrayList();
        }
        return southColumnHeaders;
    }

    public SouthTableColumnHeader3 getSouthColumnHeaderFor(final TableColumnBase<?,?> col) {
        if (col == null) {
            return null;
        }

        return southColumnHeadersByColumn.get(col);
    }

    /***************************************************************************
     * Protected methods                                                       *
     **************************************************************************/

    /** {@inheritDoc} */
    @Override protected void layoutChildren() {
        super.layoutChildren();
        double x = snappedLeftInset();
        double h = getHeight() - snappedTopInset() - snappedBottomInset();
        double fixedColumnWidth = 0;
        double hbarValue = skin.getHBar().getValue();

        for (int i = 0, max = getSouthColumnHeaders().size(); i < max; i++) {
            SouthTableColumnHeader3 n = getSouthColumnHeaders().get(i);
            if (! n.isVisible()) continue;

            double prefWidth = snapSize(n.prefWidth(-1));
            TableColumn<?, ?> column = (TableColumn<?, ?>) n.getTableColumn();
            while (column.getParentColumn() != null) {
                column = (TableColumn<?, ?>) column.getParentColumn();
            }
            double tableCellX = 0;
            if (control.isColumnFixingEnabled() && control.getFixedColumns().contains(column)) {
                //If the column is hidden we have to translate it
                if (hbarValue + fixedColumnWidth > x) {
                    tableCellX = Math.abs(hbarValue + fixedColumnWidth - x);
                    n.toFront();
                    fixedColumnWidth += prefWidth;
                }
            }
            n.relocate(x + tableCellX, snappedTopInset());
            n.resize(prefWidth, snapSize(h));
            x += prefWidth;
        }
    }

    protected void updateScrollX() {
        requestLayout();
    }

    /***************************************************************************
     * Private Implementation                                                  *
     **************************************************************************/

    private void updateSouthHeaders() {
        List<? extends TableColumnBase<?, ?>> visibleLeafColumns = control.getVisibleLeafColumns();
        // Create set of visible columns
        Set<TableColumnBase<?, ?>> visibleColumns = Collections.newSetFromMap(new IdentityHashMap<>());
        visibleColumns.addAll(visibleLeafColumns);

        // dispose headers from map for removed/non-visible columns
        southColumnHeadersByColumn.entrySet().removeIf(entry -> {
            if (visibleColumns.contains(entry.getKey())) {
                return false;
            }
            entry.getValue().dispose();
            return true;
        });

        List<SouthTableColumnHeader3> orderedHeaders = new ArrayList<>(visibleLeafColumns.size());
        for (TableColumnBase<?, ?> visibleLeafColumn : visibleLeafColumns) {
            SouthTableColumnHeader3 header = southColumnHeadersByColumn.get(visibleLeafColumn);
            if (header == null) {
                // create headers for newly visible columns and cache in map
                header = new SouthTableColumnHeader3(visibleLeafColumn);
                southColumnHeadersByColumn.put(visibleLeafColumn, header);
            }
            orderedHeaders.add(header);
        }

        // keep reordered list of headers, matching current visible-leaf order
        if (!getSouthColumnHeaders().equals(orderedHeaders)) {
            getSouthColumnHeaders().setAll(orderedHeaders);
        }
        if (!getChildren().equals(orderedHeaders)) {
            getChildren().setAll(orderedHeaders);
        }
    }

    private void updateSouthHeaderRowStyle() {
        if (control.isSouthHeaderBlended() && ! getStyleClass().contains(SOUTH_HEADER_STYLE)) {
            getStyleClass().add(SOUTH_HEADER_STYLE);
        } else if (! control.isSouthHeaderBlended() && getStyleClass().contains(SOUTH_HEADER_STYLE)) {
            getStyleClass().remove(SOUTH_HEADER_STYLE);
        }
    }

    /** {@inheritDoc} */
    @Override protected double computePrefHeight(double width) {
        double height = getSouthColumnHeaders().stream()
                .map(f -> f.prefHeight(-1))
                .max(Double::compare)
                .orElse(0d);
        if (parentSouthHeader != null) {
            final double parentHeight = parentSouthHeader.getHeight();
            if (parentHeight == 0d) {
                height = 0;
            } else {
                height = Math.max(height, parentHeight);
            }
        }
        return height + snappedTopInset() + snappedBottomInset();
    }

}
