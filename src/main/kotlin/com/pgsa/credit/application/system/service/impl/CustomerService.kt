package com.pgsa.credit.application.system.service.impl

import com.pgsa.credit.application.system.entity.Customer
import com.pgsa.credit.application.system.repository.CustomerRepository
import com.pgsa.credit.application.system.service.ICustomerService
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class CustomerService(
    private val customerRepository: CustomerRepository
): ICustomerService {
    override fun save(customer: Customer): Customer = this.customerRepository.save(customer)

    override fun findById(id: Long): Customer = this.customerRepository.findById(id).orElseThrow{
        throw RuntimeException("Id not found")
    }

    override fun delete(id: Long) = this.customerRepository.deleteById(id)
}