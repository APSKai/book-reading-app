package com.example.app1;

import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;

public class LongTextTableCell<T> extends TableCell<T, String> {

    public LongTextTableCell() {
        super();
        setOnMouseEntered(event -> {
            String text = getText();
            if (text != null && !text.isEmpty()) {
                Tooltip tooltip = new Tooltip(text);
                setTooltip(tooltip);
            }
        });
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText("");
            setTooltip(null);
        } else {
            setText(item);
        }
    }
}