package com.yourcompany.demo.model.Model;

import com.yourcompany.demo.model.Action.YearOfReleaseValidationAction;
import com.yourcompany.demo.model.Enum.RentalStatus;
import lombok.Getter;
import lombok.Setter;
import org.openxava.annotations.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@View(name="MovieDetailView", members =
        "title; " +
                "description; " +
                "yearOfRelease; " +
                "rating; " +
                "lengthOfTheMovie; " +
                "director; " +
                "language; " +
                "amount"+
                "rentalStatus"+
                "actorNames"
)
@Tab(properties = "title, description, yearOfRelease, ratingDisplay, lengthOfTheMovie, director, language, rentalStatus, amount, actorNames")
public class Movies {

    @Id
    @Hidden
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @OnChange(YearOfReleaseValidationAction.class)
    private LocalDate yearOfRelease;

    private int rating;

    private String lengthOfTheMovie;

    private String director;

    private String language;

    @Money
    @Required
    private BigDecimal amount;


    @Enumerated(EnumType.STRING)
    @Required
    private RentalStatus rentalStatus;

    @ManyToMany
    @NewAction("")
    private Collection<Actors> actors;


    @Depends("actors")
    @Hidden
    public String getActorNames() {
        if (actors == null || actors.isEmpty()) return "";
        return actors.stream()
                .map(a -> a.getFirstName() + " " + a.getLastName())
                .collect(Collectors.joining(", "));
    }

    @Depends("rating")
    @ReadOnly
    @LabelFormat(LabelFormatType.SMALL)
    public String getRatingDisplay() {
        return rating + "/10";
    }
}
