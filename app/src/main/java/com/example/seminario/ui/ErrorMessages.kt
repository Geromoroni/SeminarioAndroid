package com.example.seminario.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.seminario.R
import com.example.seminario.data.error.AppError

// La pantalla elige el mensaje según el tipo de error (strings.xml, traducible)
@Composable
fun AppError.toMessage(): String = when (this) {
    AppError.NoConnection -> stringResource(R.string.error_no_connection)
    AppError.Unauthorized -> stringResource(R.string.error_unauthorized)
    AppError.NotFound -> stringResource(R.string.error_not_found)
    is AppError.Server -> stringResource(R.string.error_server, code)
    AppError.Empty -> stringResource(R.string.error_empty)
    AppError.Unexpected -> stringResource(R.string.error_unexpected)
}
