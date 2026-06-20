package com.crescenzi.exception

import com.google.devtools.ksp.processing.KSPLogger

fun throwError(logger: KSPLogger, message: String){
    logger.error(message)
}
