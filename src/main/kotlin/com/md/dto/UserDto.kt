package com.md.dto

import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotEmpty

data class UserDto (

    @ApiModelProperty(notes = "username", required = true)
    @field: NotEmpty
    val username: String,

    @ApiModelProperty(notes = "password", required = true)
    @field: NotEmpty
    val password: String
)