package com.fakecloud.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "machine")
@Data
public class Machine {

    @Version
    private Long version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uid")
    private String uid;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MachineStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "active")
    private boolean active;

    @Column(name = "processed")
    private boolean processed;

    @Override
    public String toString() {
        return "Machine{" +
                "uid='" + uid + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", active=" + active +
                ", processed=" + processed +
                '}';
    }
}
