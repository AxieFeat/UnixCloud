package net.unix.api.modification.extension.annotation

import net.unix.api.modification.CloudAnnotationProcessor
import net.unix.api.modification.createByAnnotation
import net.unix.api.modification.extension.Extension
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.tools.StandardLocation

@SupportedAnnotationTypes("net.unix.api.modification.extension.annotation.ExtensionInfo")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class ExtensionAnnotationProcessor : CloudAnnotationProcessor() {
    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {

        val elements: Set<Element> = roundEnv.getElementsAnnotatedWith(
            ExtensionInfo::class.java
        )

        return when {
            elements.size > 1 -> {
                raiseError("Found more than one extension main class")
                return false
            }

            elements.isEmpty() -> false

            else -> {
                val mainModuleElement = elements.iterator().next()

                if (mainModuleElement !is TypeElement) {
                    raiseError("Main extension class is not a class", mainModuleElement)
                    return false
                }

                when {
                    mainModuleElement.enclosingElement !is PackageElement -> {
                        this.raiseError("Main extension class is not a top-level class", mainModuleElement)
                        return false
                    }

                    mainModuleElement.modifiers.contains(Modifier.STATIC) -> {
                        this.raiseError("Main extension class cannot be static nested", mainModuleElement)
                        return false
                    }

                    else -> {
                        if (!processingEnv.typeUtils.isSubtype(
                                mainModuleElement.asType(), this.fromClass(
                                    Extension::class.java
                                )
                            )
                        ) {
                            this.raiseError("Main extension class is not an subclass of JavaPlugin!", mainModuleElement)
                        }

                        if (mainModuleElement.modifiers.contains(Modifier.ABSTRACT)) {
                            this.raiseError("Main extension class cannot be abstract", mainModuleElement)
                            return false
                        }

                        this.checkForNoArgsConstructor(mainModuleElement)

                        val mainName = mainModuleElement.qualifiedName.toString()
                        val annotation = mainModuleElement.getAnnotation(ExtensionInfo::class.java)!!

                        val file =
                            processingEnv.filer.createResource(
                                StandardLocation.CLASS_OUTPUT,
                                "",
                                "extension.yml",
                                *arrayOfNulls(0)
                            )

                        val writer = file.openWriter()

                        writer.append("# Auto-generated extension.txt, generated at ").append(
                            LocalDateTime.now().format(
                                DateTimeFormatter.ofPattern(
                                    "yyyy/MM/dd HH:mm:ss",
                                    Locale.ENGLISH
                                ))
                        ).append(" by ").append(
                            this.javaClass.name
                        ).append("\n\n")

                        writer.append(net.unix.api.modification.extension.ExtensionInfo.createByAnnotation(mainName, annotation).toString())
                        writer.flush()

                        writer.close()

                        return true
                    }
                }
            }
        }
    }
}