package com.md.model.dto

import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotEmpty

data class EmailDto (

    @ApiModelProperty(notes = "subject", required = true)
    @field: NotEmpty
    val subject: String,

    @ApiModelProperty(notes = "subject", required = true)
    @field: NotEmpty
    val message: String
)