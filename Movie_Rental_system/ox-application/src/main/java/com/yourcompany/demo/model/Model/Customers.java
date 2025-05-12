package com.yourcompany.demo.model.Model;

import com.yourcompany.demo.model.Enum.Gender;
import lombok.Getter;
import lombok.Setter;
import org.openxava.annotations.*;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@View(members = "customerDetails [ " +
        "firstName;" +
        "lastName;" +
        "gender; " +
        "primaryAddress["+
        "addressLine1; " +
        "addressLine2; " +
        "city; " +
        "pin; " +
        "];"+
        "contactNumber; " +
        "dateOfBirth; " + "]"
)
public class Customers {

    @Id
    @Hidden
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30)
    private String firstName;

    @Column(length = 30)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Required
    private Gender gender;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    @Column(length = 10)
    private int pin;

    @Column(length = 20)
    private String contactNumber;

    private LocalDate dateOfBirth;

}
