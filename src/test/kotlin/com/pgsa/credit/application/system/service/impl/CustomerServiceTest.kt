package com.pgsa.credit.application.system.service.impl

import com.pgsa.credit.application.system.entity.Address
import com.pgsa.credit.application.system.entity.Customer
import com.pgsa.credit.application.system.exception.BusinessException
import com.pgsa.credit.application.system.repository.CustomerRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.*

@ExtendWith(MockKExtension::class)
class CustomerServiceTest {


    @InjectMockKs
    lateinit var customerService: CustomerService

    @MockK
    lateinit var customerRepository: CustomerRepository

    @Test
    fun saveTest() {

        val fakeCustomer = buildCustomer()
        every { customerRepository.save(any()) } returns fakeCustomer

        val result = customerService.save(fakeCustomer)

        Assertions.assertThat(result).isNotNull
        Assertions.assertThat(result).isSameAs(fakeCustomer)
        verify(exactly = 1) { customerRepository.save(fakeCustomer) }
    }

    @Test
    fun findByIdTest() {

        val fakeId = Random().nextLong()
        val fakeCustomer = buildCustomer(id = fakeId)

        every { customerRepository.findById(fakeId) } returns Optional.of(fakeCustomer)

        val result = customerService.findById(fakeId)

        Assertions.assertThat(result).isNotNull
        Assertions.assertThat(result).isSameAs(fakeCustomer)
        Assertions.assertThat(result).isExactlyInstanceOf(Customer::class.java)
        verify(exactly = 1) { customerRepository.findById(fakeId) }
    }

    @Test
    fun findByIdExceptionTest() {

        val fakeId = 2L

        every { customerRepository.findById(fakeId) } returns Optional.empty()

        Assertions.assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { customerService.findById(fakeId) }
            .withMessage("Id $fakeId not found")
        verify(exactly = 1) { customerRepository.findById(fakeId) }
    }

    @Test
    fun deleteTest() {

        val fakeId = 2L
        val fakeCustomer = buildCustomer(id = fakeId)

        every { customerRepository.findById(fakeId) } returns Optional.of(fakeCustomer)
        every { customerRepository.delete(fakeCustomer) } just runs

        customerService.delete(fakeId)

        verify(exactly = 1) { customerRepository.findById(fakeId) }
        verify(exactly = 1) { customerRepository.delete(fakeCustomer) }
    }

    companion object {
        fun buildCustomer(
            firstName: String = "Paulo",
            lastName: String = "Gustavo",
            cpf: String = "43906333418",
            email: String = "paulo@test.com",
            password: String = "1234",
            zipCode: String = "12345",
            street: String = "Rua das flores",
            income: BigDecimal = BigDecimal.valueOf(2000.0),
            id: Long = 1L
        ) = Customer(
            firstName = firstName,
            lastName = lastName,
            cpf = cpf,
            email = email,
            password = password,
            address = Address(
                zipCode = zipCode,
                street = street,
            ),
            income = income,
            id = id
        )
    }

}