import com.github.miachm.sods.*;

import java.io.File;
import java.io.IOException;

/* An example demonstrating conditional formatting features of the SODS library.
   This creates a spreadsheet with student grades and applies conditional formatting
   to highlight different grade ranges.
 */
public class ConditionalFormatting {
    public static void main(String args[]) throws IOException {
        SpreadSheet spreadSheet = new SpreadSheet();
        Sheet sheet = new Sheet("Student Grades", 11, 4);
        
        // Create headers
        Range headerRange = sheet.getRange(0, 0, 1, 4);
        headerRange.setValues("Student Name", "Math", "Science", "Average");
        
        // Style headers
        Style headerStyle = new Style();
        headerStyle.setBold(true);
        headerStyle.setBackgroundColor(new Color(70, 130, 180));
        headerStyle.setFontColor(new Color(255, 255, 255));
        headerStyle.setTextAligment(Style.TEXT_ALIGMENT.Center);
        headerRange.setStyle(headerStyle);
        
        // Add student data
        String[] students = {"Alice Johnson", "Bob Smith", "Carol Brown", "David Lee", "Eva Garcia", 
                           "Frank Wilson", "Grace Chen", "Henry Davis", "Iris Martinez", "Jack Taylor"};
        
        for (int i = 0; i < students.length; i++) {
            int row = i + 1;
            sheet.getRange(row, 0).setValue(students[i]);
            
            // Generate sample grades (60-100)
            int mathGrade = 60 + (int)(Math.random() * 41);
            int scienceGrade = 60 + (int)(Math.random() * 41);
            
            sheet.getRange(row, 1).setValue(mathGrade);
            sheet.getRange(row, 2).setValue(scienceGrade);
            
            // Calculate average using formula
            sheet.getRange(row, 3).setFormula("AVERAGE(B" + (row + 1) + ":C" + (row + 1) + ")");
        }
        
        // Create conditional formatting rules using static methods
        ConditionalFormat excellentFormat = ConditionalFormat.conditionWhenValueIsGreater(
            createGradeStyle(new Color(144, 238, 144)), 90); // Light green for >=90
        
        ConditionalFormat goodFormat = ConditionalFormat.conditionWhenValueIsGreater(
            createGradeStyle(new Color(255, 255, 153)), 80); // Light yellow for >=80
        
        ConditionalFormat poorFormat = ConditionalFormat.conditionWhenValueIsLower(
            createGradeStyle(new Color(255, 182, 193)), 70); // Light pink for <70
        
        // Apply conditional formatting to individual grade cells
        for (int i = 1; i <= 10; i++) {
            for (int j = 1; j <= 3; j++) { // Math, Science, and Average columns
                Style cellStyle = new Style();
                cellStyle.addCondition(excellentFormat);
                cellStyle.addCondition(goodFormat);
                cellStyle.addCondition(poorFormat);
                cellStyle.setBorders(new Borders(true));
                sheet.getRange(i, j).setStyle(cellStyle);
            }
        }
        
        // Add borders to student name column only (grades already have borders from gradeStyle)
        Style nameColumnStyle = new Style();
        nameColumnStyle.setBorders(new Borders(true));
        sheet.getRange(1, 0, 10, 1).setStyle(nameColumnStyle); // Student name column
        
        // Headers already have styling applied earlier, but ensure they have borders too
        headerStyle.setBorders(new Borders(true));
        headerRange.setStyle(headerStyle);
        
        // Set column widths for better display
        sheet.setColumnWidth(0, 30.0); // Student name column wider
        sheet.setColumnWidth(1, 20.0); // Math column
        sheet.setColumnWidth(2, 20.0); // Science column
        sheet.setColumnWidth(3, 20.0); // Average column
        
        spreadSheet.appendSheet(sheet);
        spreadSheet.save(new File("conditional-formatting-example.ods"));
        
        System.out.println("Spreadsheet with conditional formatting created: conditional-formatting-example.ods");
        System.out.println("Open it in LibreOffice Calc to see the conditional formatting in action!");
    }
    
    private static Style createGradeStyle(Color backgroundColor) {
        Style style = new Style();
        style.setBackgroundColor(backgroundColor);
        style.setBold(true);
        return style;
    }
}