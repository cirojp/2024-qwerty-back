package api.back;

import jakarta.persistence.*;

import java.util.Map;

@Entity
@Table(name = "category_budgets")
public class CategoryBudgets {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = MapToJsonConverter.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> categories;

    public CategoryBudgets() {
    }

    public CategoryBudgets(Map<String, String> categories) {
        this.categories = categories;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, String> getCategories() {
        return categories;
    }

    public void setCategories(Map<String, String> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "CategoryBudgets{" +
                "id=" + id +
                ", categories=" + categories +
                '}';
    }
}
