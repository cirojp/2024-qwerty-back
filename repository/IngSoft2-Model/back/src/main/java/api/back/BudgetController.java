package api.back;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/presupuesto")
public class BudgetController {

    @Autowired
    private BudgetRepository budgetRepository;

    @PostMapping
    public ResponseEntity<Void> postNuevoPresupuesto(@RequestBody Budget presupuesto,
            Authentication authentication) {
        budgetRepository.save(presupuesto);
        return ResponseEntity.ok().build();
    }

}
