package com.github.miachm.sods.examples;

import com.github.miachm.sods.spreadsheet.Range;
import com.github.miachm.sods.spreadsheet.Sheet;
import com.github.miachm.sods.spreadsheet.SpreadSheet;
import com.github.miachm.sods.spreadsheet.Style;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/*
This sample implements a very simple ODS reader in JavaFX.

The programs starts asking the user for an ODS file,
after that, it loads and render it at the screen.
 */

public class OdsGuiViewer extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            File file = askUserForAnOdsFile();
            if (file == null) {
                primaryStage.close();
                Platform.exit();
                return;
            }

            SpreadSheet spreadSheet = new SpreadSheet(file);
            Parent output = renderOdsView(spreadSheet);
            
            Scene scene = new Scene(output, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Ods viewer");
            primaryStage.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File askUserForAnOdsFile()
    {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("."));

        FileChooser.ExtensionFilter extFilter = new
                FileChooser.ExtensionFilter("Spreadsheet files", "*.ods");
        chooser.getExtensionFilters().add(extFilter);

        return chooser.showOpenDialog(null);
    }

    private Parent renderOdsView(SpreadSheet spreadSheet)
    {
        TabPane tabPane = new TabPane();
        for (Sheet sheet : spreadSheet.getSheets()) {
            Tab tab = renderSheetView(sheet);
            tabPane.getTabs().add(tab);
        }

        return tabPane;
    }

    private Tab renderSheetView(Sheet sheet)
    {
        Tab tab = new Tab(sheet.getName());

        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        gridPane.setMinWidth(600);
        gridPane.setMinHeight(300);

        Range range = sheet.getDataRange();
        Object[][] values = range.getValues();
        Style[][] styles = range.getStyles();

        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                Object value = values[i][j];
                Style style = styles[i][j];
                gridPane.add(renderCellView(value, style), j, i);
            }
        }

        ScrollPane scrollPane = new ScrollPane(gridPane);
        tab.setContent(scrollPane);

        return tab;
    }

    private Node renderCellView(Object value, Style style)
    {
        Label label;
        if (value != null)
            label = new Label("" + value);
        else
            label = new Label(" ");

        Node node = label;
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setMinWidth(40);
        label.setMinHeight(10);

        if (!style.isDefault()) {
            // Get Css representation with the JavaFX prefix
            String css = style.toString("-fx-");

            // Workaround, the text-color property in JavaFX is different of normal CSS
            if (style.getFontColor() != null)
                css += "-fx-text-fill: " + style.getFontColor();

            label.setStyle(css);

            // Workaround, the background color of the label doesn't fit the entire cell
            // So, we wrap the label in a pane which it grows with the cell.
            if (style.getBackgroundColor() != null)
            {
                Pane pane = new Pane();
                pane.setStyle("-fx-background-color: " + style.getBackgroundColor());
                label.layoutXProperty().bind(pane.widthProperty().subtract(label.widthProperty()).divide(2));
                label.layoutYProperty().bind(pane.heightProperty().subtract(label.heightProperty()).divide(2));
                pane.getChildren().add(label);
                GridPane.setMargin(pane, new Insets(1,1,1,1));
                node = pane;
            }
        }

        // Center the text of the cell
        GridPane.setHalignment(node, HPos.CENTER);

        // Responsive cells
        GridPane.setHgrow(node, Priority.ALWAYS);
        GridPane.setVgrow(node, Priority.ALWAYS);

        return node;
    }
}
