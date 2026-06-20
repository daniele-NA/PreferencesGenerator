package com.crescenzi


import com.crescenzi.core.Values
import com.crescenzi.exception.throwError
import com.crescenzi.helper.buildFlows
import com.crescenzi.helper.buildImports
import com.crescenzi.helper.buildParams
import com.crescenzi.helper.buildResetMethod
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration


class PreferencesProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return PreferencesProcessor(environment.codeGenerator, environment.logger)
    }
}


class PreferencesProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    // == qualified names of the allowed preference annotations == //
    private val allowedAnnotationNames: Set<String?> =
        allowedAnnotations.map { it.canonicalName }.toSet()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver
            .getSymbolsWithAnnotation(GeneratePreferences::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .forEach { typeElement -> generateFor(typeElement) }

        return emptyList()
    }


    private fun generateFor(typeElement: KSClassDeclaration) {
        val packageName = typeElement.packageName.asString()
        val rawInterfaceName = typeElement.simpleName.asString()
        val interfaceName = "$rawInterfaceName${Values.CLASS_SUFFIX}"

        val prefName = typeElement.annotations
            .first { it.shortName.asString() == GeneratePreferences::class.simpleName }
            .arguments
            .first { it.name?.asString() == "preferencesName" }
            .value
            .toString()
            .trim()

        if (prefName.isEmpty()) {
            throwError(logger, "Invalid or Empty Preferences Name")
            return
        }


        val prefList = mutableListOf<Pref<*>>()


        val members: Sequence<KSDeclaration> =
            typeElement.getDeclaredProperties() + typeElement.getDeclaredFunctions()

        members.forEach { member ->
            val key = member.simpleName.asString()
                .removePrefix("get")
                .replaceFirstChar { it.lowercase() }

            val mirror = prefAnnotationOf(member) ?: return@forEach

            val annotation =
                mirror.annotationType.resolve().declaration.qualifiedName?.asString()
            val defValue = mirror.arguments
                .firstOrNull { it.name?.asString() == Values.PREF_VAL_NAME }
                ?.value

            when (annotation) {
                StringPref::class.qualifiedName -> prefList.add(
                    prefFactory(
                        defaultValue = defValue as String,
                        key = key
                    )
                )

                IntPref::class.qualifiedName -> prefList.add(
                    prefFactory(
                        defaultValue = defValue as Int,
                        key = key
                    )
                )

                BooleanPref::class.qualifiedName -> prefList.add(
                    prefFactory(
                        defaultValue = defValue as Boolean,
                        key = key
                    )
                )

                LongPref::class.qualifiedName -> prefList.add(
                    prefFactory(
                        defaultValue = defValue as Long,
                        key = key
                    )
                )

                FloatPref::class.qualifiedName -> prefList.add(
                    prefFactory(
                        defaultValue = defValue as Float,
                        key = key
                    )
                )

                DoublePref::class.qualifiedName -> prefList.add(
                    prefFactory(
                        defaultValue = defValue as Double,
                        key = key
                    )
                )
            }
        }

        if (prefList.isEmpty()) {
            throwError(
                logger,
                "No annotated preference fields found in interface: $rawInterfaceName"
            )
            return
        }


        val imports = buildImports()
        val params = indent(buildParams(logger, prefList), 8)  // == inside companion object == //
        val flows = indent(buildFlows(prefList), 4)    // == inside class == //
        val resetMethod = indent(buildResetMethod(prefList), 4)


        codeGenerator.createNewFile(  // == create .kt file == //
            dependencies = Dependencies(aggregating = false, typeElement.containingFile!!),
            packageName = packageName,
            fileName = interfaceName
        ).bufferedWriter().use { writer ->
            writer.write(
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


    /* ==
    Retrieves the preference annotation on a member.
    In Kotlin the annotations are `@get:...` (on the getter), in Java they are on the method:
    they are looked up in both places.
    == */
    private fun prefAnnotationOf(member: KSDeclaration): KSAnnotation? {
        val candidates = when (member) {
            is KSPropertyDeclaration ->
                member.annotations + (member.getter?.annotations ?: emptySequence())

            else -> member.annotations
        }

        return candidates.firstOrNull { ann ->
            ann.annotationType.resolve().declaration.qualifiedName?.asString() in allowedAnnotationNames
        }
    }
}


/* ==
Adds indentation to each line of the text.
@param text The block of text to indent
@param spaces Number of spaces to add at the start of each line
== */
fun indent(text: String, spaces: Int = 4): String {
    val pad = " ".repeat(spaces)
    return text.lineSequence()
        .joinToString("\n") { line ->
            if (line.isBlank()) "" else pad + line.trimEnd()
        }
}
