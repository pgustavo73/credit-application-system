package com.pgsa.credit.application.system.repository

import com.pgsa.credit.application.system.entity.Credits
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface CreditRepository: JpaRepository<Credits, Long> {

    fun findByCreditCode(creditCode: UUID): Credits?

    @Query(value = "SELECT * FROM CREDITS WHERE CUSTOMER_ID = ?1", nativeQuery = true)
    fun findAllByCutomersId(customerID: Long): List<Credits>
}