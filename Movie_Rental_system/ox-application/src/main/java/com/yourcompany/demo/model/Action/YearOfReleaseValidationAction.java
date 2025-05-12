package com.yourcompany.demo.model.Action;

import org.openxava.actions.OnChangePropertyBaseAction;

import java.time.LocalDate;
import java.time.Year;

public class YearOfReleaseValidationAction extends OnChangePropertyBaseAction {

    @Override
    public void execute() throws Exception {
        Object value = getNewValue();
        if (value == null) return;


        LocalDate selectedDate = (LocalDate) value;
        LocalDate currentDate = LocalDate.now();

        if (!selectedDate.isBefore(currentDate)) {
            addError("The movie must have been released in the past — not today or in the future.");
        }
    }
}
