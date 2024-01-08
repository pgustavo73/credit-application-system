package com.pgsa.credit.application.system.dto

import com.pgsa.credit.application.system.entity.Credits
import com.pgsa.credit.application.system.entity.Customer
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDto(
    val creditValue: BigDecimal,
    val dayFirstOfInstallment: LocalDate,
    val numberOfInstallment: Int,
    val customerId: Long
){

    fun toEntity(): Credits = Credits(
        creditValue =  this.creditValue,
        dayFirstInstallment = this.dayFirstOfInstallment,
        numberOfInstallment = this.numberOfInstallment,
        customer = Customer(id = this.customerId)
    )
}
