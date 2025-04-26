import com.github.miachm.sods.Range;
import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;
import com.github.miachm.sods.Style;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/*
This sample implements a very simple ODS reader in Swing.

The program starts by asking the user for an ODS file,
after that, it loads and renders it on the screen.
 */

public class OdsGuiViewer {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                File file = askUserForAnOdsFile();
                if (file == null) {
                    System.exit(0);
                    return;
                }

                System.out.println("Loading...");
                SpreadSheet spreadSheet = new SpreadSheet(file);
                System.out.println("Rendering...");
                JComponent output = renderOdsView(spreadSheet);
                System.out.println("Creating window...");

                JFrame frame = new JFrame("ODS Viewer");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(output);
                frame.setSize(800, 600);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception e) {
                showExceptionDialog(e);
                e.printStackTrace();
            }
        });
    }

    private static File askUserForAnOdsFile() {
        JFileChooser chooser = new JFileChooser(".");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Spreadsheet files", "ods");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    private static JComponent renderOdsView(SpreadSheet spreadSheet) {
        JTabbedPane tabbedPane = new JTabbedPane();
        for (Sheet sheet : spreadSheet.getSheets()) {
            JComponent sheetView = renderSheetView(sheet);
            tabbedPane.addTab(sheet.getName(), sheetView);
        }
        return tabbedPane;
    }

    private static JComponent renderSheetView(Sheet sheet) {
        // Create a non-editable table model
        DefaultTableModel model = new NonEditableTableModel(
                sheet.getMaxRows(), sheet.getMaxColumns());

        // Create a custom table that can handle cell merging
        class MergeableJTable extends JTable {
            private final Map<Integer, Map<Integer, CellSpan>> cellSpans = new HashMap<>();

            public MergeableJTable(TableModel model) {
                super(model);
            }

            @Override
            public void paint(Graphics g) {
                // Custom rendering to handle merged cells
                super.paint(g);
            }

            @Override
            public Rectangle getCellRect(int row, int column, boolean includeSpacing) {
                Rectangle rect = super.getCellRect(row, column, includeSpacing);

                // Adjust rectangle for merged cells
                if (cellSpans.containsKey(row) && cellSpans.get(row).containsKey(column)) {
                    CellSpan span = cellSpans.get(row).get(column);

                    if (span.rowSpan > 1 || span.colSpan > 1) {
                        rect = new Rectangle(rect);
                        for (int r = 1; r < span.rowSpan; r++) {
                            rect.height += getRowHeight(row + r);
                        }
                        for (int c = 1; c < span.colSpan; c++) {
                            rect.width += getColumnModel().getColumn(column + c).getWidth();
                        }
                    }
                }

                return rect;
            }

            public void setCellSpan(int row, int column, int rowSpan, int colSpan) {
                if (!cellSpans.containsKey(row)) {
                    cellSpans.put(row, new HashMap<>());
                }
                cellSpans.get(row).put(column, new CellSpan(rowSpan, colSpan));
            }
        }

        MergeableJTable table = new MergeableJTable(model);

        // Set up the table appearance
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setGridColor(Color.GRAY);
        table.setBorder(new LineBorder(Color.BLACK));

        // Fill in data and apply styles
        for (int i = 0; i < sheet.getMaxRows(); i++) {
            for (int j = 0; j < sheet.getMaxColumns(); j++) {
                Range range = sheet.getRange(i, j);

                // Handle merged cells
                if (range.isPartOfMerge() && isHeadOfMerge(range)) {
                    Range group = range.getMergedCells()[0];
                    int rows = group.getNumRows();
                    int cols = group.getNumColumns();
                    table.setCellSpan(i, j, rows, cols);
                }

                // Set cell values
                if (hasContent(range)) {
                    Object value = range.getValue();
                    if (value != null) {
                        model.setValueAt(value, i, j);
                    }

                    // Apply styles (handled by custom cell renderer in production code)
                    // For simplicity, we're not implementing the full styling in this example
                }
            }
        }

        // Set column widths
        for (int i = 0; i < sheet.getMaxColumns(); i++) {
            Double width = sheet.getColumnWidth(i);
            if (width != null) {
                table.getColumnModel().getColumn(i).setPreferredWidth((int)(width * 3.77));
            }
        }

        // Set row heights
        for (int i = 0; i < sheet.getMaxRows(); i++) {
            Double height = sheet.getRowHeight(i);
            if (height != null) {
                table.setRowHeight(i, (int)(height * 3.77));
            }
        }

        // Create a scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        return scrollPane;
    }

    private static boolean hasContent(Range range) {
        if (range.isPartOfMerge()) {
            return isHeadOfMerge(range);
        }

        Object value = range.getValue();
        if (value != null && !value.toString().isEmpty()) {
            return true;
        }

        Style style = range.getStyle();
        return !style.isDefault();
    }

    private static boolean isHeadOfMerge(Range range) {
        if (range.isPartOfMerge()) {
            Range group = range.getMergedCells()[0];
            return group.getRow() == range.getRow() &&
                    group.getColumn() == range.getColumn();
        }
        return false;
    }

    public static void showExceptionDialog(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String msg = t.getMessage();
        String exception = sw.toString();
        showExceptionDialog(msg, exception);
    }

    public static void showExceptionDialog(String msg, String exception) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);

        // Create a detailed error dialog
        JDialog dialog = new JDialog((Frame)null, "Exception Details", true);
        dialog.setLayout(new BorderLayout());

        JLabel label = new JLabel("Backtrace:");

        JTextArea textArea = new JTextArea(exception);
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(label, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    // Helper class for non-editable table model
    private static class NonEditableTableModel extends DefaultTableModel {
        public NonEditableTableModel(int rows, int columns) {
            super(rows, columns);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    // Helper class for tracking cell spans
    private static class CellSpan {
        int rowSpan;
        int colSpan;

        public CellSpan(int rowSpan, int colSpan) {
            this.rowSpan = rowSpan;
            this.colSpan = colSpan;
        }
    }
}