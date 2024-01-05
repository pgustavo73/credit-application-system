package com.pgsa.credit.application.system.entity

import com.pgsa.credit.application.system.enums.Status
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Entity
data class Credits (
    @Column(nullable = false, unique = true) val creditCode: UUID = UUID.randomUUID(),
    @Column(nullable = false) val creditValue: BigDecimal = BigDecimal.ZERO,
    @Column(nullable = false) val dayFirstInstallment: LocalDate,
    @Column(nullable = false) val numberOfInstallment: Int,
    @Enumerated val status: Status,
    @ManyToOne var customer: Customer?,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long?,
)
