package com.pgsa.credit.application.system.repository

import com.pgsa.credit.application.system.entity.Credits
import org.springframework.data.jpa.repository.JpaRepository

interface creditRepository: JpaRepository<Credits, Long> {
}