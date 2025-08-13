package code.exception


import javax.annotation.processing.ProcessingEnvironment
import javax.tools.Diagnostic



fun throwError(processingEnv: ProcessingEnvironment, message: String){
    processingEnv.messager.printMessage(
        Diagnostic.Kind.ERROR,
        message
    )

}