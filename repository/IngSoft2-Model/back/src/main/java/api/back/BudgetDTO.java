package api.back;

import java.util.Map;

public class BudgetDTO {
    private Long id;
    private String nameBudget;
    private int totalBudget;
    private Map<String, Integer> categoryBudgets;
    private String user; // solo el email
    private String budgetMonth;
    private String payOptionBudget;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameBudget() {
        return nameBudget;
    }

    public void setNameBudget(String nameBudget) {
        this.nameBudget = nameBudget;
    }

    public int getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(int totalBudget) {
        this.totalBudget = totalBudget;
    }

    public Map<String, Integer> getCategoryBudgets() {
        return categoryBudgets;
    }

    public void setCategoryBudgets(Map<String, Integer> categoryBudgets) {
        this.categoryBudgets = categoryBudgets;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBudgetMonth() {
        return budgetMonth;
    }

    public void setBudgetMonth(String budgetMonth) {
        this.budgetMonth = budgetMonth;
    }

    public String getPayOptionBudget() {
        return payOptionBudget;
    }

    public void setPayOptionBudget(String payOptionBudget) {
        this.payOptionBudget = payOptionBudget;
    }

    // Constructor
    public BudgetDTO(Budget budget) {
        this.id = budget.getId();
        this.nameBudget = budget.getNameBudget();
        this.totalBudget = budget.getTotalBudget();
        this.categoryBudgets = budget.getCategoryBudgets();
        this.user = budget.getUser().getEmail();
        this.budgetMonth = budget.getBudgetMonth();
        this.payOptionBudget = budget.getPayOptionBudget();
    }

    // Getters y setters si los necesitás
}
