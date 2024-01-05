package com.pgsa.credit.application.system.service.impl

import com.pgsa.credit.application.system.entity.Credits
import com.pgsa.credit.application.system.repository.CreditRepository
import com.pgsa.credit.application.system.service.ICreditService
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.util.*

@Service
class CreditService(
    private val creditRepository: CreditRepository,
    private val customerService: CustomerService
): ICreditService {
    override fun save(credit: Credits): Credits {
        credit.apply {
            customer = customerService.findById(credit.customer?.id!!)
        }
        return this.creditRepository.save(credit)
    }

    override fun findAllByCustumer(customerId: Long): List<Credits> =
        this.creditRepository.findAllByCutomersId(customerId)

    override fun findByCreditCode(custumerId: Long, creditCode: UUID): Credits {
        val credit: Credits = this.creditRepository.findByCreditCode(creditCode)
            ?: throw RuntimeException("CreditCode $creditCode not found")
        return if (credit.customer?.id == custumerId) credit else throw RuntimeException("Contact admin")
    }
}