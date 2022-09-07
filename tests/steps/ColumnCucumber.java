package steps;

import cucumber.api.java.en.When;

public class ColumnCucumber {

    @When("^the client appends a column$")
    public void the_client_appends_a_column() throws Throwable {
        World.sheet.appendColumn();
    }

    @When("^the client appends (\\d+) columns$")
    public void the_client_appends_columns(int columns) throws Throwable {
        World.sheet.appendColumns(columns);
    }

    @When("^the client appends (-?\\d+) columns and catch the exception$")
    public void the_client_appends_columns_and_catch_the_exception(int columns) throws Throwable {
        try {
            World.sheet.appendColumns(columns);
        }
        catch (IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client deletes the column (-?\\d+) and catch the exception$")
    public void the_client_deletes_the_column_and_catch_the_exception(int column) throws Throwable {
        try {
            World.sheet.deleteColumn(column);
        }
        catch (IndexOutOfBoundsException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client deletes the column (\\d+)$")
    public void the_client_deletes_the_column(int column) throws Throwable {
        World.sheet.deleteColumn(column);
    }

    @When("^the client deletes (\\d+) columns on the index (\\d+)$")
    public void the_client_deletes_columns_on_the_index(int howmany, int column) throws Throwable {
        World.sheet.deleteColumns(column, howmany);
    }

    @When("^the client deletes the column (\\d+) on the index (-?\\d+) and catch the exception$")
    public void the_client_deletes_the_column_on_the_index_and_catch_the_exception(int howmany, int column) throws Throwable {
        try {
            World.sheet.deleteColumns(column, howmany);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client inserts a column before the index (\\d+)$")
    public void the_client_inserts_a_column_before_the_index(int index) throws Throwable {
        World.sheet.insertColumnBefore(index);
    }

    @When("^the client inserts a column after the index (\\d+) and catch the exception$")
    public void the_client_inserts_a_column_after_the_index_and_catch_the_exception(int index) throws Throwable {
        try {
            World.sheet.insertColumnAfter(index);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client inserts a column after the index (\\d+)$")
    public void the_client_inserts_a_column_after_the_index(int index) throws Throwable {
        World.sheet.insertColumnAfter(index);
    }

    @When("^the client inserts (\\d+) columns before the index (\\d+)$")
    public void the_client_inserts_columns_before_the_index(int columns, int index) throws Throwable {
        World.sheet.insertColumnsBefore(index, columns);
    }

    @When("^the client inserts (\\d+) columns after the index (\\d+)$")
    public void the_client_inserts_columns_after_the_index(int columns, int index) throws Throwable {
        World.sheet.insertColumnsAfter(index, columns);
    }

    @When("^the client inserts (-\\d+) columns before the index (\\d+) and catch the exception$")
    public void the_client_inserts_columns_before_the_index_and_catch_the_exception(int columns, int index) throws Throwable {
        try {
            World.sheet.insertColumnsBefore(index, columns);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client inserts (-\\d+) columns after the index (\\d+) and catch the exception$")
    public void the_client_inserts_columns_after_the_index_and_catch_the_exception(int columns, int index) throws Throwable {
        try {
            World.sheet.insertColumnsAfter(index, columns);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }
}
