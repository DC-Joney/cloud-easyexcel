package com.chamc.archtype.budget.service;

import com.chamc.archtype.budget.excel.event.ActualCapitalEvent;
import com.chamc.archtype.budget.excel.event.CapitalPlanEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;


@Service(value = "budgetService")
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BudgetService {


    @EventListener(CapitalPlanEvent.class)
    public void listenEvent(CapitalPlanEvent event){
        System.out.println(event.getSource());
    }


    @EventListener(ActualCapitalEvent.class)
    public void listenEvent(ActualCapitalEvent event){
        System.out.println(event.getSource());
    }

    public boolean selectOne(String name) {
        return true;
    }

}
