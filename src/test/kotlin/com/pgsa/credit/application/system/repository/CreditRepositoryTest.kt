package com.pgsa.credit.application.system.repository

import com.pgsa.credit.application.system.entity.Address
import com.pgsa.credit.application.system.entity.Credits
import com.pgsa.credit.application.system.entity.Customer
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import java.util.*

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CreditRepositoryTest {
    @Autowired
    lateinit var creditRepository: CreditRepository
    @Autowired
    lateinit var testEntityManager: TestEntityManager

    private lateinit var customer: Customer
    private lateinit var credits1: Credits
    private lateinit var credits2: Credits

    @BeforeEach
    fun setup() {
        customer = testEntityManager.persist(buildCustomer())
        credits1 = testEntityManager.persist(buildCredit(customer = customer))
        credits2 = testEntityManager.persist(buildCredit(customer = customer))
    }

    @Test
    fun findByCreditCodeTest() {

        val creditCode1 = UUID.fromString("aa547c0f-9a6a-451f-8c89-afddce916a29")
        val creditCode2 = UUID.fromString("49f740be-46a7-449b-84e7-ff5b7986d7ef")
        credits1.creditCode = creditCode1
        credits2.creditCode = creditCode2

        val mockcredit1 = creditRepository.findByCreditCode(creditCode1)!!
        val mockcredit2 = creditRepository.findByCreditCode(creditCode2)!!

        Assertions.assertThat(mockcredit1).isNotNull
        Assertions.assertThat(mockcredit2).isNotNull
        Assertions.assertThat(mockcredit1).isSameAs(credits1)
        Assertions.assertThat(mockcredit2).isSameAs(credits2)
    }

    @Test
    fun findAllByCutomersIdTest() {

        val customerId = 1L

        val listCredits = creditRepository.findAllByCutomersId(customerId)

        Assertions.assertThat(listCredits).isNotEmpty
        Assertions.assertThat(listCredits).contains(credits1, credits2)
        Assertions.assertThat(listCredits.size).isEqualTo(2)

    }


    private fun buildCredit(
        creditValue: BigDecimal = BigDecimal.valueOf(900.0),
        dayFirstInstallment: LocalDate = LocalDate.of(2023, Month.JUNE, 15),
        numberOfInstallments: Int = 8,
        customer: Customer
    ): Credits = Credits(
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallment = numberOfInstallments,
        customer = customer
    )
    private fun buildCustomer(
        firstName: String = "Paulo",
        lastName: String = "Gustavo",
        cpf: String = "43906333418",
        email: String = "paulo@test.com",
        password: String = "1234",
        zipCode: String = "12345",
        street: String = "Rua das flores",
        income: BigDecimal = BigDecimal.valueOf(2000.0)
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
    )
}