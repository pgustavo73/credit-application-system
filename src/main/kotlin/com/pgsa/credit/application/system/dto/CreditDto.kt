package com.pgsa.credit.application.system.dto

import com.pgsa.credit.application.system.entity.Credits
import com.pgsa.credit.application.system.entity.Customer
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDto(
    @field:NotNull(message = "income must be fill") val creditValue: BigDecimal,
    @field:Future(message = "Date must to be in the future") val dayFirstOfInstallment: LocalDate,
    @field:Max(value = 48, message = "Number of installment should be max of 48") val numberOfInstallment: Int,
    @field:NotNull(message = "customerId must be fill") val customerId: Long
) {

    fun toEntity(): Credits = Credits(
        creditValue = this.creditValue,
        dayFirstInstallment = this.dayFirstOfInstallment,
        numberOfInstallment = this.numberOfInstallment,
        customer = Customer(id = this.customerId)
    )
}
