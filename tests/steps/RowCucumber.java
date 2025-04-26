package steps;

import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

public class RowCucumber {

    @When("^the client appends a row$")
    public void the_client_appends_a_row() throws Throwable {
        World.sheet.appendRow();
    }

    @When("^the client appends (-?\\d+) rows$")
    public void the_client_appends_rows(int rows) throws Throwable {
        World.sheet.appendRows(rows);
    }

    @When("^the client appends (-?\\d+) rows and catch the exception$")
    public void the_client_appends_rows_and_catch_the_exception(int rows) throws Throwable {
        try {
            World.sheet.appendRows(rows);
        }
        catch (IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client deletes the row (\\d+) and catch the exception$")
    public void the_client_deletes_the_row_and_catch_the_exception(int row) throws Throwable {
        try {
            World.sheet.deleteRow(row);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client deletes the row (\\d+)$")
    public void the_client_deletes_the_row(int row) throws Throwable {
        World.sheet.deleteRow(row);
    }

    @When("^the client deletes (\\d+) rows on the index (\\d+)$")
    public void the_client_deletes_rows_on_the_index(int howmany, int row) throws Throwable {
        World.sheet.deleteRows(row, howmany);
    }

    @When("^the client deletes the row (\\d+) on the index (-?\\d+) and catch the exception$")
    public void the_client_deletes_the_row_on_the_index_and_catch_the_exception(int howmany, int row) throws Throwable {
        try {
            World.sheet.deleteRows(row, howmany);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client inserts a row before the index (\\d+)$")
    public void the_client_inserts_a_row_before_the_index(int index) throws Throwable {
        World.sheet.insertRowBefore(index);
    }

    @When("^the client inserts a row after the index (\\d+) and catch the exception$")
    public void the_client_inserts_a_row_after_the_index_and_catch_the_exception(int index) throws Throwable {
        try {
            World.sheet.insertRowAfter(index);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client inserts a row after the index (\\d+)$")
    public void the_client_inserts_a_row_after_the_index(int index) throws Throwable {
        World.sheet.insertRowAfter(index);
    }

    @When("^the client inserts (\\d+) rows before the index (\\d+)$")
    public void the_client_inserts_rows_before_the_index(int howmany, int index) throws Throwable {
        World.sheet.insertRowsBefore(index, howmany);
    }

    @When("^the client inserts (\\d+) rows after the index (\\d+)$")
    public void the_client_inserts_rows_after_the_index(int howmany, int index) throws Throwable {
        World.sheet.insertRowsAfter(index, howmany);
    }

    @When("^the client inserts (-\\d+) rows before the index (\\d+) and catch the exception$")
    public void the_client_inserts_rows_before_the_index_and_catch_the_exception(int howmany, int index) throws Throwable {
        try {
            World.sheet.insertRowsBefore(index, howmany);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client inserts (-\\d+) rows after the index (\\d+) and catch the exception$")
    public void the_client_inserts_rows_after_the_index_and_catch_the_exception(int howmany, int index) throws Throwable {
        try {
            World.sheet.insertRowsAfter(index, howmany);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }
}
