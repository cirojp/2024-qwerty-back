package api.back;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {
    @Autowired
    private BudgetRepository budgetRepository;

    public List<Budget> getPresupuestosByUserId(User user) {
        return budgetRepository.findByUser(user);
    }

    public void save(Budget presupuesto) {
        budgetRepository.save(presupuesto);
    }
}
