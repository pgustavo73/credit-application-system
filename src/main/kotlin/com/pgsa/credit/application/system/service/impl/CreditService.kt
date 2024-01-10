package com.pgsa.credit.application.system.service.impl

import com.pgsa.credit.application.system.entity.Credits
import com.pgsa.credit.application.system.exception.BusinessException
import com.pgsa.credit.application.system.repository.CreditRepository
import com.pgsa.credit.application.system.service.ICreditService
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.time.LocalDate
import java.util.*

@Service
class CreditService(
    private val creditRepository: CreditRepository,
    private val customerService: CustomerService
): ICreditService {
    override fun save(credit: Credits): Credits {
        this.validDayFirstInstallment(credit.dayFirstInstallment)
        credit.apply {
            customer = customerService.findById(credit.customer?.id!!)
        }
        return this.creditRepository.save(credit)
    }

    override fun findAllByCustumer(customerId: Long): List<Credits> =
        this.creditRepository.findAllByCutomersId(customerId)

    override fun findByCreditCode(custumerId: Long, creditCode: UUID): Credits {
        val credit: Credits = this.creditRepository.findByCreditCode(creditCode)
            ?: throw BusinessException("CreditCode $creditCode not found")
        return if (credit.customer?.id == custumerId) credit else throw BusinessException("Contact admin")
    }

    private fun validDayFirstInstallment(dayFirstInstallment: LocalDate): Boolean {
        return if (dayFirstInstallment.isBefore(LocalDate.now().plusMonths(3))) true
        else throw BusinessException("Invalid Date")
    }
}