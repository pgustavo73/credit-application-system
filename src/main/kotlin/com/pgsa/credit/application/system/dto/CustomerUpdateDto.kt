package com.pgsa.credit.application.system.dto

import com.pgsa.credit.application.system.entity.Customer
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class CustomerUpdateDto(
    @field:NotEmpty(message = "firstName must be fill") val firstName: String,
    @field:NotEmpty(message = "lastName must be fill") val lastName: String,
    @field:NotNull(message = "income must be fill") val income: BigDecimal,
    @field:NotEmpty(message = "zipCode must be fill") val zipCode: String,
    @field:NotEmpty(message = "street must be fill") val street: String
) {
    fun toEntity(customer: Customer): Customer {
        customer.firstName = this.firstName
        customer.lastName = this.lastName
        customer.income = this.income
        customer.address.zipCode = this.zipCode
        customer.address.street = this.street
        return customer
    }
}
