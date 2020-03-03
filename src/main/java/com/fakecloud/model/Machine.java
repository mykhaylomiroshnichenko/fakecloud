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

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "active")
    private boolean active;

    @Override
    public String toString() {
        return "Machine{" +
                "uid='" + uid + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", active=" + active +
                '}';
    }
}
