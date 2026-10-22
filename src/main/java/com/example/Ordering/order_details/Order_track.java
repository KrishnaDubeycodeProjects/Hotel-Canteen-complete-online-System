package com.example.Ordering.order_details;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_track")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order_track {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Orders order;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "order_status")
    private Order_Status orderStatus;

    private String updatedBy;
    private String remarks;
    private LocalDateTime lastUpdateTime = LocalDateTime.now();
}
