package api.back;

import jakarta.persistence.*;
import java.util.Map;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer totalBudget;

    @ElementCollection
    @CollectionTable(name = "category_budgets", joinColumns = @JoinColumn(name = "budget_id"))
    @MapKeyColumn(name = "category")
    @Column(name = "budget")
    private Map<String, Integer> categoryBudgets;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Relación con la entidad User

    public Budget(Integer totalBudget, Map<String, Integer> categoryBudgets, User user) {
        this.totalBudget = totalBudget;
        this.categoryBudgets = categoryBudgets;
        this.user = user;
    }

    public Budget(Integer totalBudget, Map<String, Integer> categoryBudgets) {
        this.totalBudget = totalBudget;
        this.categoryBudgets = categoryBudgets;
    }

    public Budget() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(Integer totalBudget) {
        this.totalBudget = totalBudget;
    }

    public Map<String, Integer> getCategoryBudgets() {
        return categoryBudgets;
    }

    public void setCategoryBudgets(Map<String, Integer> categoryBudgets) {
        this.categoryBudgets = categoryBudgets;
    }

    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", totalBudget=" + totalBudget +
                ", categoryBudgets=" + categoryBudgets +
                '}';
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
