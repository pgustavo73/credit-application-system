package com.pgsa.credit.application.system.repository

import com.pgsa.credit.application.system.entity.Customer
import org.springframework.data.jpa.repository.JpaRepository

interface customerRepository: JpaRepository<Customer, Long> {
}