package com.hub.schoolAid;

import javafx.scene.control.ListCell;

public class TransactionListCell extends ListCell{
    @Override
    protected void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);
        this.setItem(item.toString());
    }
}
