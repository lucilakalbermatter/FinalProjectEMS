package com.finalproject.model.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"leavesRequests"})
@ToString(exclude = {"leavesRequests"})
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

    @Enumerated(value = EnumType.STRING)
    private LeaveRequestStatus leaveRequestStatus;

    @Enumerated(value = EnumType.STRING)
    private LeaveStatus leaveStatus;

    @Column(name = "start_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;

    @Column(name = "end_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "leaves", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Set<User> users;

    @OneToMany(mappedBy = "leave", cascade = CascadeType.ALL)
    private Set<LeaveRequest> leaveRequests;

}
