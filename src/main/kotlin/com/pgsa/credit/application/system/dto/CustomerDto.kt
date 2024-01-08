package com.pgsa.credit.application.system.dto

import com.pgsa.credit.application.system.entity.Address
import com.pgsa.credit.application.system.entity.Customer
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.br.CPF
import java.math.BigDecimal

data class CustomerDto(
    @field:NotEmpty(message = "firstName must be fill") val fistName: String,
    @field:NotEmpty(message = "lastName must be fill") val lastName: String,
    @field:NotEmpty(message = "cpf must be fill")
    @field:CPF(message = "invaid CPF") val cpf: String,
    @field:NotNull(message = "income must be fill") val income: BigDecimal,
    @field:NotEmpty(message = "email must be fill")
    @field:Email(message = "Invalid email") val email: String,
    @field:NotEmpty(message = "password must be fill") val password: String,
    @field:NotEmpty(message = "zipCode must be fill") val zipCode: String,
    @field:NotEmpty(message = "street must be fill") val street: String
) {

    fun toEntity(): Customer = Customer(
        firstName = this.fistName,
        lastName = this.lastName,
        cpf = this.cpf,
        income = this.income,
        email = this.email,
        password = this.password,
        address = Address(
            zipCode = this.zipCode,
            street = this.street,
        )
    )
}
