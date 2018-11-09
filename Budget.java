package Dir.dev.mywallet.Model;



public class Budget {
    private int budgetId;
    private Double budgetAmount;
    private String budgetCategory;
    private String budgetMonth;
    private String budgetYear;

    public Budget() {
    }

    public Budget(int budgetId, Double budgetAmount, String budgetCategory, String budgetMonth, String budgetYear) {
        this.budgetId = budgetId;
        this.budgetAmount = budgetAmount;
        this.budgetCategory = budgetCategory;
        this.budgetMonth = budgetMonth;
        this.budgetYear = budgetYear;
    }

    public int getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
    }

    public Double getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(Double budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public String getBudgetCategory() {
        return budgetCategory;
    }

    public void setBudgetCategory(String budgetCategory) {
        this.budgetCategory = budgetCategory;
    }

    public String getBudgetMonth() {
        return budgetMonth;
    }

    public void setBudgetMonth(String budgetMonth) {
        this.budgetMonth = budgetMonth;
    }

    public String getBudgetYear() {
        return budgetYear;
    }

    public void setBudgetYear(String budgetYear) {
        this.budgetYear = budgetYear;
    }
}
