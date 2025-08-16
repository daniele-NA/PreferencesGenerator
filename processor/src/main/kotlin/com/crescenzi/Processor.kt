package com.crescenzi


import com.crescenzi.core.Values
import com.crescenzi.exception.throwError
import com.crescenzi.helper.buildFlows
import com.crescenzi.helper.buildImports
import com.crescenzi.helper.buildParams
import com.crescenzi.helper.buildResetMethod
import com.google.auto.service.AutoService
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.tools.StandardLocation


@AutoService(Processor::class)
class Processor : AbstractProcessor() {

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(GeneratePreferences::class.java.canonicalName)
    }


    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        roundEnv!!.getElementsAnnotatedWith(GeneratePreferences::class.java)
            .filterIsInstance<TypeElement>().forEach { typeElement ->
                val packageName =
                    (typeElement.enclosingElement as PackageElement).qualifiedName.toString()
                val rawInterfaceName= typeElement.simpleName
                val interfaceName = "$rawInterfaceName${Values.CLASS_SUFFIX}"

                val prefName =
                    typeElement.getAnnotation(GeneratePreferences::class.java).preferencesName.trim()

                if (prefName.isEmpty()) throwError(processingEnv, "Invalid or Empty Preferences Name")


                val prefList = mutableListOf<Pref<*>>()


                typeElement.enclosedElements
                    .forEach { method ->
                        val key = method.simpleName.toString()
                            .removePrefix("get")
                            .replaceFirstChar { it.lowercase() }

                        val mirror = method.annotationMirrors.firstOrNull { m ->
                            allowedAnnotations.any { ann -> m.annotationType.toString() == ann.canonicalName }
                        }

                        if (mirror != null) {
                            val annotation = mirror.annotationType.toString()
                            val defValue = mirror.elementValues.entries
                                .firstOrNull { it.key.simpleName.toString() == Values.PREF_VAL_NAME }
                                ?.value?.value

                            when (annotation) {
                                StringPref::class.java.canonicalName -> prefList.add(
                                    prefFactory(
                                        defaultValue = defValue as String,
                                        key = key
                                    )
                                )

                                IntPref::class.java.canonicalName -> prefList.add(
                                    prefFactory(
                                        defaultValue = defValue as Int,
                                        key = key
                                    )
                                )

                                BooleanPref::class.java.canonicalName -> prefList.add(
                                    prefFactory(
                                        defaultValue = defValue as Boolean,
                                        key = key
                                    )
                                )

                                LongPref::class.java.canonicalName -> prefList.add(
                                    prefFactory(
                                        defaultValue = defValue as Long,
                                        key = key
                                    )
                                )

                                FloatPref::class.java.canonicalName -> prefList.add(
                                    prefFactory(
                                        defaultValue = defValue as Float,
                                        key = key
                                    )
                                )

                                DoublePref::class.java.canonicalName -> prefList.add(
                                    prefFactory(
                                        defaultValue = defValue as Double,
                                        key = key
                                    )
                                )
                            }
                        }
                    }

                if (prefList.isEmpty()) throwError(
                    processingEnv,
                    "No annotated preference fields found in interface: $rawInterfaceName"
                )


                val imports = buildImports()
                val params = indent(buildParams(processingEnv,prefList), 8)  // dentro companion object
                val flows = indent(buildFlows(prefList), 4)    // dentro classe
                val resetMethod= indent(buildResetMethod(prefList), 4)


                val fileObject = processingEnv.filer.createResource( // Crea un file .Kt
                    StandardLocation.SOURCE_OUTPUT,
                    packageName,
                    "$interfaceName.kt"
                )


                fileObject.openWriter().use { writer ->
                    writer.apply {
                        write(
                            """
package $packageName

$imports

class $interfaceName(val context: Context) {

    companion object {
    
     ${params.trim()}
    
    }

    private val Context.dataStore by preferencesDataStore(name = "$prefName")
    private val prefs = context.dataStore
    private val scope = CoroutineScope(Dispatchers.IO)

 
    ${flows.trim()}
    
    fun <T> updatePreference(key: Preferences.Key<T>, value: T) {
        scope.launch{
            prefs.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    ${resetMethod.trim()}
 
}
                    """.trimIndent()
                        )
                    }
                }
            }
        return true
    }


}

/**
 * Aggiunge indentazione a ciascuna riga del testo.
 * @param text Il blocco di testo da indentare
 * @param spaces Numero di spazi da aggiungere a inizio riga
 */
fun indent(text: String, spaces: Int = 4): String {
    val pad = " ".repeat(spaces)
    return text.lineSequence()
        .joinToString("\n") { line ->
            if (line.isBlank()) "" else pad + line.trimEnd()
        }
}