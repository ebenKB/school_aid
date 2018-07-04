package com.hub.schoolAid;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;

public class CustomCombo {
    private static CustomCombo ourInstance = new CustomCombo();

    public static CustomCombo getInstance() {
        return ourInstance;
    }

    private CustomCombo() {
    }

    public void overrideCombo(ComboBox combo){
        // Define rendering of the list of values in ComboBox drop down.
        combo.setCellFactory((comboBox) -> {
            return new ListCell<Stage>() {
                @Override
                protected void updateItem(Stage item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            };
        });

// Define rendering of selected value shown in ComboBox.
        combo.setConverter(new StringConverter<Stage>() {
            @Override
            public String toString(Stage stage) {
                if (stage == null) {
                    return null;
                } else {
                    return stage.getName();
                }
            }

            @Override
            public Stage fromString(String stageString) {
                return null; // No conversion fromString needed.
            }
        });
    }
}
