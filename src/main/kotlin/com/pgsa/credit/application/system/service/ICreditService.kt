package com.pgsa.credit.application.system.service

import com.pgsa.credit.application.system.entity.Credits
import java.util.*

interface ICreditService {

    fun save(credit: Credits): Credits

    fun findAllByCustumer(customerId: Long): List<Credits>

    fun findByCreditCode(customerId: Long, creditCode: UUID): Credits
}