package com.md.repository

import com.md.model.StudFac
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface StudFacRepository : JpaRepository<StudFac, UUID> {
    fun findByStudidAndFacid(stud_id: UUID, fac_id: UUID): Optional<StudFac>
    fun findByFacid(fac_id: UUID): List<StudFac>
}