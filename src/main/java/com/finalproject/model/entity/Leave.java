package com.finalproject.model.entity;

import com.finalproject.util.converter.DurationConverter;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString()
@Entity
@Table(name = "leaves")
public class Leave {
    @Id
    @SequenceGenerator(name = "leaveIdSeq", sequenceName = "leaves_id_seq", allocationSize = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leaveIdSeq")
    @Column(name = "id")
    private Long id;

    @Column(length = 500)
    private String description;

    @Enumerated(value = EnumType.STRING)
    private LeaveReason leaveReason;

    @Column(name = "start_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @Column(name = "end_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    @Column(name = "duration")
    @Convert(converter = DurationConverter.class)
    private Duration duration;

    @ManyToOne
    private User employee;

    private String supervisor;

    private String status;

    private boolean statusUpdated;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Leave leave = (Leave) o;
        return id != null && Objects.equals(id, leave.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
