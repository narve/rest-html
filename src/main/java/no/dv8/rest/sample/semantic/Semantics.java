package no.dv8.rest.sample.semantic;

public interface Semantics {

    String colStart = "_";
    String colEnd = "";

    String ExpenseReport = "ExpenseReport";
    String ExpenseReportCollection = "Collection" + colStart + ExpenseReport + colEnd;
    String User = "User";
    String UserCollection = "Collection" + colStart + User + colEnd;
    String Id = "id";
    String Self = "Self";

    String PROPSEP = ".";
    String Item = "item";

    static String collectionOf(String itemTypeName) {
        return "Collection" + colStart + itemTypeName + colEnd;
    }

    default String join(String... propNames) {
        return String.join(PROPSEP, propNames);
    }
}
