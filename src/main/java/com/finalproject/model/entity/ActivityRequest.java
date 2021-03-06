package com.finalproject.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "activity_requests")
public class ActivityRequest {
    @Id
    @SequenceGenerator(name = "activityRequestIdSeq", sequenceName = "activity_requests_id_seq", allocationSize = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "activityRequestIdSeq")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    private Activity activity;

    @Column(name = "action")
    @Enumerated(value = EnumType.STRING)
    private ActivityRequestAction action;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private ActivityRequestStatus status;

    @Column(name = "request_date")
    private LocalDateTime requestDate;
}
