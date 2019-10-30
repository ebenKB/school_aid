package com.hub.schoolAid;

import javafx.scene.control.ListCell;

public class SaleListCell extends ListCell<Sales> {
    @Override
    protected void updateItem(Sales item, boolean empty) {
        super.updateItem(item, empty);

        if (item != null) {
            this.setText(item.toString());
        }
    }
}