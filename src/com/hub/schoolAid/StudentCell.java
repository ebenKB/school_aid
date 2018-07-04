package com.hub.schoolAid;

import javafx.scene.control.ListCell;

public class StudentCell extends ListCell<Student> {
    @Override
    protected void updateItem(Student item, boolean empty) {
        super.updateItem(item, empty);
        int index = this.getIndex();
        String name = null;

        //format the name
        if(item ==null ||empty){

        }else {
            name = (index+1) +"."+item.toString();
        }
        this.setText(name);
        setGraphic(null);
    }
}
