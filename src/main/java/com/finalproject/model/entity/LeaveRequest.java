package com.finalproject.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "leave_requests")
public class LeaveRequest {
    @Id
    @SequenceGenerator(name = "leaveRequestIdSeq", sequenceName = "leaverequests_id_seq", allocationSize = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator ="leaveRequestIdSeq")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    private Leave leave;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private LeaveRequestStatus status;

    @Column(name = "request_date")
    private LocalDate requestDate;
}