package com.md.model.dto

import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotEmpty

data class KeyValueUpdateObjectDto (

    @ApiModelProperty(notes = "subject", required = true)
    @field: NotEmpty
    var key: String,

    @ApiModelProperty(notes = "subject", required = true)
    @field: NotEmpty
    var value: String
)