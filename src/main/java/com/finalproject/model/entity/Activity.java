package com.finalproject.model.entity;

import com.finalproject.util.converter.DurationConverter;
import com.finalproject.util.converter.LocalDateTimeConverter;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "activities")
public class Activity {
    @Id
    @SequenceGenerator(name = "activityIdSeq", sequenceName = "activities_id_seq", allocationSize = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "activityIdSeq")
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(value = EnumType.STRING)
    private ActivityImportance importance;

    @Enumerated(value = EnumType.STRING)
    private ActivityStatus status;

    @Column(name = "start_time")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime endTime;

    @Column(name = "duration")
    @Convert(converter = DurationConverter.class)
    private Duration duration;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "activities", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Set<User> users;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<ActivityRequest> activityRequests;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Activity activity = (Activity) o;
        return id != null && Objects.equals(id, activity.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
