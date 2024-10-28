package api.back;

import jakarta.persistence.*;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String totalBudget;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_budgets_id", referencedColumnName = "id")
    private CategoryBudgets categoryBudgets;

    public Budget() {
    }

    public Budget(String totalBudget, CategoryBudgets categoryBudgets) {
        this.totalBudget = totalBudget;
        this.categoryBudgets = categoryBudgets;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(String totalBudget) {
        this.totalBudget = totalBudget;
    }

    public CategoryBudgets getCategoryBudgets() {
        return categoryBudgets;
    }

    public void setCategoryBudgets(CategoryBudgets categoryBudgets) {
        this.categoryBudgets = categoryBudgets;
    }

    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", totalBudget='" + totalBudget + '\'' +
                ", categoryBudgets=" + categoryBudgets +
                '}';
    }
}
