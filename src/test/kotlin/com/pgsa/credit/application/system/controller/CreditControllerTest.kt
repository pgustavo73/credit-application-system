package com.pgsa.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.pgsa.credit.application.system.dto.CreditDto
import com.pgsa.credit.application.system.dto.CustomerDto
import com.pgsa.credit.application.system.repository.CreditRepository
import com.pgsa.credit.application.system.repository.CustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditControllerTest {

    @Autowired
    private lateinit var creditRepository: CreditRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    companion object {
        const val URL = "/api/credits"
        const val IVALID_URL = "/api/"
    }

    @BeforeEach
    fun setup() {
        creditRepository.deleteAll()
        customerRepository.deleteAll()
    }

    @AfterEach
    fun tearDown() {
        creditRepository.deleteAll()
        customerRepository.deleteAll()
    }

    @Test
    fun saveCreditStatus201() {

        val customer = customerRepository.save(buildCustomerDTO().toEntity())
        val creditDto = buildCreditDTo(customer = customer.id!!.toLong())
        val valueString = objectMapper.writeValueAsString(creditDto)

        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueString)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value(900))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallment").value(8))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("IN_PROGRESS"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer").value("paulo@test.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.incomeCustomer").value(2000))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun saveCreditStatus400() {

        customerRepository.save(buildCustomerDTO().toEntity())
        val creditDto = buildCreditDTo(customer = 10L)
        val valueString = objectMapper.writeValueAsString(creditDto)

        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult Documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class com.pgsa.credit.application.system.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun saveCreditStatus404() {

        customerRepository.save(buildCustomerDTO().toEntity())
        val creditDto = buildCreditDTo()
        val valueString = objectMapper.writeValueAsString(creditDto)

        mockMvc.perform(
            MockMvcRequestBuilders.post(IVALID_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueString)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun findAllByCustomerIdStatus200() {

        val customer = customerRepository.save(buildCustomerDTO().toEntity())
        creditRepository.save(buildCreditDTo(customer = customer.id!!.toLong()).toEntity())

        mockMvc.perform(
            MockMvcRequestBuilders.get(URL)
                .param("customerId", "${customer.id}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0]creditCode").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0]creditValue").value(900))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0]numberOfInstallment").value(8))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun findAllByCustomerIdStatus400() {

        customerRepository.save(buildCustomerDTO().toEntity())

        mockMvc.perform(
            MockMvcRequestBuilders.get(URL)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun findByCreditCodeStatus200() {

        val customer = customerRepository.save(buildCustomerDTO().toEntity())
        val credit = creditRepository.save(buildCreditDTo(customer = customer.id!!.toLong()).toEntity())

        mockMvc.perform(
            MockMvcRequestBuilders.get("${URL}/${credit.creditCode}")
                .param("customerId", "${customer.id}")
                .contentType(MediaType.APPLICATION_JSON)
        )

            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditCode").value("${credit.creditCode}"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value(900))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallment").value(8))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("IN_PROGRESS"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer").value("paulo@test.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.incomeCustomer").value(2000))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun findByCreditCodeStatus400() {

        val customer = customerRepository.save(buildCustomerDTO().toEntity())
        val credit = creditRepository.save(buildCreditDTo(customer = customer.id!!.toLong()).toEntity())

        mockMvc.perform(
            MockMvcRequestBuilders.get("${URL}/${credit.creditCode}")
                .param("customerId", "20")
                .contentType(MediaType.APPLICATION_JSON)
        )

            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult Documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class com.pgsa.credit.application.system.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    private fun buildCreditDTo(
        creditValue: BigDecimal = BigDecimal.valueOf(900.0),
        dayFirstInstallment: LocalDate = LocalDate.of(2024, Month.MARCH, 15),
        numberOfInstallment: Int = 8,
        customer: Long = 1L
    ): CreditDto = CreditDto(
        creditValue = creditValue,
        dayFirstOfInstallment = dayFirstInstallment,
        numberOfInstallment = numberOfInstallment,
        customerId = customer
    )

    private fun buildCustomerDTO(
        firstName: String = "Paulo",
        lastName: String = "Gustavo",
        cpf: String = "43906333418",
        email: String = "paulo@test.com",
        password: String = "1234",
        zipCode: String = "12345",
        street: String = "Rua das flores",
        income: BigDecimal = BigDecimal.valueOf(2000.0)
    ) = CustomerDto(
        fistName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        income = income,
        password = password,
        zipCode = zipCode,
        street = street
    )

}