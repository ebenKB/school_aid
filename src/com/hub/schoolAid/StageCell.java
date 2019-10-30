package com.hub.schoolAid;

import javafx.scene.control.ListCell;

public class StageCell extends ListCell<Stage> {
    @Override
    protected void updateItem(Stage item, boolean empty) {
        super.updateItem(item, empty);
        int index = this.getIndex();
        String name = null;

        //format the name
        if(item ==null ||empty){

        }else {
//            name = (index+1) +"."+item.toString(); // return the name of the item with numbering
            name = item.toString(); // return the name of the item only
        }
        this.setText(name);
        setGraphic(null);
    }
}

