package com.yourcompany.demo.model.Model;

import com.yourcompany.demo.model.Enum.Gender;
import lombok.Getter;
import lombok.Setter;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.Required;
import org.openxava.annotations.View;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Getter
@Setter
@View(members = "Actors [" +
        "firstName;"+
        "lastName;"+
        "gender;" +
        "]")
public class Actors {

    @Id
    @Hidden
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @Enumerated(EnumType.STRING)
    @Required
    private Gender gender;


    @ManyToMany(mappedBy = "actors")
    private Collection<Movies> movies;

}
