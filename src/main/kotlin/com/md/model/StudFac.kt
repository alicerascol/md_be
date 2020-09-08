package com.md.model

import java.io.Serializable
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.Table

@IdClass(StudFacId::class)
@Entity
@Table(name = "stud_fac_table")
data class StudFac(
    @Id
    val studid: UUID,

    @Id
    val facid: UUID,

    var status: StudentStatus = StudentStatus.REGISTERED
): Serializable {

    override fun toString(): String {
        return "stud_fac_table(stud_id=$studid, " +
                "fac_id=$facid, " +
                "status=$status )"
    }
}

class StudFacId(
    val studid: UUID,
    val facid: UUID
) : Serializable {
    constructor() : this(UUID.randomUUID(), UUID.randomUUID())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StudFacId

        return other.studid === studid && other.facid === facid
    }

    override fun hashCode() = Objects.hash(studid)

}
