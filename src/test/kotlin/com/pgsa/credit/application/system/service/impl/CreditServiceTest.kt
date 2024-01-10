package com.pgsa.credit.application.system.service.impl

import com.pgsa.credit.application.system.entity.Credits
import com.pgsa.credit.application.system.entity.Customer
import com.pgsa.credit.application.system.exception.BusinessException
import com.pgsa.credit.application.system.repository.CreditRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@ExtendWith(MockKExtension::class)
class CreditServiceTest {

    @InjectMockKs
    lateinit var creditService: CreditService

    @MockK
    lateinit var customerService: CustomerService

    @MockK
    lateinit var creditRepository: CreditRepository

    @Test
    fun saveTest() {
        val fakeCredits = buildCredit()
        val customerId = 1L

        every { customerService.findById(customerId) } returns fakeCredits.customer!!
        every { creditRepository.save(fakeCredits) } returns fakeCredits

        val result = creditService.save(fakeCredits)

        Assertions.assertThat(result).isNotNull
        Assertions.assertThat(result).isSameAs(fakeCredits)
        verify(exactly = 1) { creditRepository.save(fakeCredits) }
        verify(exactly = 1) { customerService.findById(customerId) }
        verify(exactly = 1) { creditRepository.save(fakeCredits) }

    }

    @Test
    fun saveWithInvalidFirstInstallmentTest() {

        val invalidDayFirstInstallment: LocalDate = LocalDate.now().plusMonths(4)
        val credit: Credits = buildCredit(dayFirstInstallment = invalidDayFirstInstallment)

        every { creditRepository.save(credit) } answers { credit }

        Assertions.assertThatThrownBy { creditService.save(credit) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("Invalid Date")

        verify(exactly = 0) { creditRepository.save(any()) }

    }

    @Test
    fun findAllByCustumerTest() {

        val customerId = 1L
        val expectedCredits: List<Credits> = listOf(buildCredit(), buildCredit(), buildCredit())

        every { creditRepository.findAllByCutomersId(any()) } returns expectedCredits

        val result = creditService.findAllByCustumer(customerId)

        Assertions.assertThat(result).isNotNull
        Assertions.assertThat(result).isSameAs(expectedCredits)
        Assertions.assertThat(result).isNotEmpty
        verify(exactly = 1) { creditRepository.findAllByCutomersId(customerId) }
    }

    @Test
    fun findByCreditCodeTest() {

        val customerId = 1L
        val creditCode: UUID = UUID.randomUUID()
        val credit: Credits = buildCredit(customer = Customer(id = customerId))

        every { creditRepository.findByCreditCode(creditCode) } returns credit

        val result = creditService.findByCreditCode(customerId, creditCode)

        Assertions.assertThat(result).isNotNull
        Assertions.assertThat(result).isSameAs(credit)
        verify(exactly = 1) { creditRepository.findByCreditCode(creditCode) }
    }

    @Test
    fun findByCreditCodeWithExceptionTest() {

        val customerId = 2L
        val creditCode: UUID = UUID.randomUUID()
        val credit: Credits = buildCredit(customer = Customer(id = 1L))

        every { creditRepository.findByCreditCode(creditCode) } returns credit


        Assertions.assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { creditService.findByCreditCode(customerId, creditCode) }
            .withMessage("Contact admin")
        verify(exactly = 1) { creditRepository.findByCreditCode(creditCode) }
    }

    @Test
    fun findByCreditCodeWithExceptionForInvalidCreditCodeTest() {

        val customerId = 2L
        val creditCode: UUID = UUID.randomUUID()

        every { creditRepository.findByCreditCode(creditCode) } returns null


        Assertions.assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { creditService.findByCreditCode(customerId, creditCode) }
            .withMessage("CreditCode $creditCode not found")
        verify(exactly = 1) { creditRepository.findByCreditCode(creditCode) }
    }


    companion object {
        private fun buildCredit(
            creditValue: BigDecimal = BigDecimal.valueOf(100.0),
            dayFirstInstallment: LocalDate = LocalDate.now().plusMonths(2L),
            numberOfInstallments: Int = 15,
            customer: Customer = CustomerServiceTest.buildCustomer()
        ): Credits = Credits(
            creditValue = creditValue,
            dayFirstInstallment = dayFirstInstallment,
            numberOfInstallment = numberOfInstallments,
            customer = customer
        )
    }
}