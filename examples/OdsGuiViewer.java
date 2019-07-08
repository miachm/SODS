import com.github.miachm.sods.Range;
import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;
import com.github.miachm.sods.Style;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

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

            System.out.println("Loading...");
            SpreadSheet spreadSheet = new SpreadSheet(file);
            System.out.println("Rendering...");
            Parent output = renderOdsView(spreadSheet);
            System.out.println("Creating window...");

            Scene scene = new Scene(output, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Ods viewer");
            primaryStage.show();
        }
        catch (Exception e) {
            showExceptionDialog(e);
            e.printStackTrace();
        }
    }

    private File askUserForAnOdsFile()
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

        setRowHeights(sheet, gridPane);
        setColumnWidths(sheet, gridPane);

        for (int i = 0; i < sheet.getMaxRows(); i++) {
            for (int j = 0; j < sheet.getMaxColumns(); j++) {
                Range range = sheet.getRange(i, j);
                if (hasContent(range)) {
                    Node node = renderCellView(range.getValue(), range.getStyle());
                    if (range.isPartOfMerge()) {
                        Range group = range.getMergedCells()[0];
                        mergeCells(group.getNumRows(), group.getNumColumns(), node);
                    }

                    gridPane.add(node, j, i);
                }
            }
        }

        ScrollPane scrollPane = new ScrollPane(gridPane);
        tab.setContent(scrollPane);

        return tab;
    }

    private void mergeCells(int rows, int columns, Node node) {
        GridPane.setRowSpan(node, rows);
        GridPane.setColumnSpan(node, columns);
    }

    private boolean hasContent(Range range) {
        if (range.isPartOfMerge()) {
            return isHeadOfMerge(range);
        }

        Object value = range.getValue();
        if (value != null && !value.toString().isEmpty())
            return true;

        Style style = range.getStyle();
        return !style.isDefault();
    }

    private boolean isHeadOfMerge(Range range)
    {
        if (range.isPartOfMerge()) {
            Range group = range.getMergedCells()[0];
            return group.getRow() == range.getRow() &&
                    group.getColumn() == range.getColumn();
        }
        return false;
    }

    private void setColumnWidths(Sheet sheet, GridPane gridPane) {
        for (int i = 0; i < sheet.getMaxColumns(); i++) {
            ColumnConstraints columnConstraints;
            Double n = sheet.getColumnWidth(i);
            if (n != null) {
                columnConstraints = new ColumnConstraints(n*3.77);
                columnConstraints.setHgrow(Priority.NEVER);
            }
            else {
                columnConstraints = new ColumnConstraints();
            }

            gridPane.getColumnConstraints().add(columnConstraints);
        }
    }

    private void setRowHeights(Sheet sheet, GridPane gridPane) {
        for (int i = 0; i < sheet.getMaxRows(); i++) {
            RowConstraints rowConstraints;
            Double n = sheet.getRowHeight(i);

            if (n != null) {
                rowConstraints = new RowConstraints(n*3.77);
                rowConstraints.setVgrow(Priority.NEVER);
            }
            else {
                rowConstraints = new RowConstraints();
            }

            gridPane.getRowConstraints().add(rowConstraints);
        }
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

        return node;
    }

    public void showExceptionDialog(Throwable t)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String msg = t.getMessage();
        String exception = sw.toString();
        showExceptionDialog(msg, exception);
    }

    public void showExceptionDialog(String msg,String exception)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception error");
        alert.setContentText(msg);

        Label label = new Label("Backtrace: \n");

        TextArea textArea = new TextArea(exception);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);
        alert.show();
    }
}
