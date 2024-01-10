package com.pgsa.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.pgsa.credit.application.system.dto.CustomerDto
import com.pgsa.credit.application.system.dto.CustomerUpdateDto
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

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CustomerControllerTest {

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL = "/api/customers"
    }

    @BeforeEach
    fun setup() = customerRepository.deleteAll()

    @AfterEach
    fun tearDown() = customerRepository.deleteAll()

    @Test
    fun saveCustomerStatus201() {

        val customerDto = buildCustomerDTO()
        val valueString = objectMapper.writeValueAsString(customerDto)

        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueString)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Paulo"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Gustavo"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("43906333418"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("paulo@test.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("2000.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("12345"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua das flores"))
            .andDo(MockMvcResultHandlers.print())

    }

    @Test
    fun saveCustomerStatus409() {

        customerRepository.save(buildCustomerDTO().toEntity())
        val customerDto = buildCustomerDTO()
        val valueString = objectMapper.writeValueAsString(customerDto)

        mockMvc.perform(
            MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON).content(valueString)
        )
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Conflict! Consult Documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(409))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.dao.DataIntegrityViolationException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())

    }

    @Test
    fun saveCustomerStatus400() {

        val customerDto = buildCustomerDTO(firstName = "")
        val valueString = objectMapper.writeValueAsString(customerDto)

        mockMvc.perform(
            MockMvcRequestBuilders.post(URL).content(valueString).contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult Documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.web.bind.MethodArgumentNotValidException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun findByIdStatus200() {

        val customer = customerRepository.save(buildCustomerDTO().toEntity())

        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/${customer.id}").accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Paulo"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Gustavo"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("43906333418"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("paulo@test.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("2000.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("12345"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua das flores"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun findByIdStatus400() {

        val invalidId = 2L

        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/$invalidId").accept(MediaType.APPLICATION_JSON)
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
    fun deleteCustomerStatus204() {

        val customer = customerRepository.save(buildCustomerDTO().toEntity())

        mockMvc.perform(
            MockMvcRequestBuilders.delete("$URL/${customer.id}").accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andDo(MockMvcResultHandlers.print())

    }

    @Test
    fun deleteCustomerStatus400() {

        val invalidId = 1L

        mockMvc.perform(
            MockMvcRequestBuilders.delete("$URL/${invalidId}").accept(MediaType.APPLICATION_JSON)
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
    fun updateCustomerStatus200() {

        val customer = customerRepository.save(buildCustomerDTO().toEntity())
        val customerUpdateDto = builderCustomerUpdateDto()
        val valueString = objectMapper.writeValueAsString(customerUpdateDto)

        mockMvc.perform(
            MockMvcRequestBuilders.patch("$URL?customerId=${customer.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueString)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Paulo_Updated"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Gustavo_Updated"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("43906333418"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("paulo@test.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("4000.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("1234567"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua das flores_Updated"))
            .andDo(MockMvcResultHandlers.print())

    }

    @Test
    fun updateCustomerStatus400() {

        val invalidId = 1L
        val customerUpdateDto = builderCustomerUpdateDto()
        val valueString = objectMapper.writeValueAsString(customerUpdateDto)

        mockMvc.perform(
            MockMvcRequestBuilders.patch("$URL?customerId=${invalidId}")
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

    private fun builderCustomerUpdateDto(
        firstName: String = "Paulo_Updated",
        lastName: String = "Gustavo_Updated",
        income: BigDecimal = BigDecimal.valueOf(4000.0),
        zipCode: String = "1234567",
        street: String = "Rua das flores_Updated"
    ): CustomerUpdateDto = CustomerUpdateDto(
        firstName = firstName,
        lastName = lastName,
        income = income,
        zipCode = zipCode,
        street = street
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