package net.unix.api.modification.module.annotation

import net.unix.api.modification.module.CloudModule
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.*
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.ElementFilter
import javax.tools.Diagnostic
import javax.tools.StandardLocation

@SupportedAnnotationTypes("net.unix.api.module.annotation.ModuleInfo")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class ModuleAnnotationProcessor : AbstractProcessor() {
    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {

        val elements: Set<Element> = roundEnv.getElementsAnnotatedWith(
            ModuleInfo::class.java
        )

        return when {
            elements.size > 1 -> {
                raiseError("Found more than one module main class")
                return false
            }

            elements.isEmpty() -> false

            else -> {
                val mainModuleElement = elements.iterator().next()

                if (mainModuleElement !is TypeElement) {
                    raiseError("Main module class is not a class", mainModuleElement)
                    return false
                }

                when {
                    mainModuleElement.enclosingElement !is PackageElement -> {
                        this.raiseError("Main module class is not a top-level class", mainModuleElement)
                        return false
                    }

                    mainModuleElement.modifiers.contains(Modifier.STATIC) -> {
                        this.raiseError("Main module class cannot be static nested", mainModuleElement)
                        return false
                    }

                    else -> {
                        if (!processingEnv.typeUtils.isSubtype(
                                mainModuleElement.asType(), this.fromClass(
                                    CloudModule::class.java
                                )
                            )
                        ) {
                            this.raiseError("Main module class is not an subclass of JavaPlugin!", mainModuleElement)
                        }

                        if (mainModuleElement.modifiers.contains(Modifier.ABSTRACT)) {
                            this.raiseError("Main module class cannot be abstract", mainModuleElement)
                            return false
                        }

                        this.checkForNoArgsConstructor(mainModuleElement)

                        val mainName = mainModuleElement.qualifiedName.toString()
                        val annotation = mainModuleElement.getAnnotation(ModuleInfo::class.java)!!

                        val file =
                            processingEnv.filer.createResource(
                                StandardLocation.CLASS_OUTPUT,
                                "",
                                "module.txt",
                                *arrayOfNulls(0)
                            )

                        val writer = file.openWriter()

                        writer.append("# Auto-generated module.txt, generated at ").append(
                            LocalDateTime.now().format(
                                DateTimeFormatter.ofPattern(
                                    "yyyy/MM/dd HH:mm:ss",
                                    Locale.ENGLISH
                                ))
                        ).append(" by ").append(
                            this.javaClass.name
                        ).append("\n\n")

                        writer.append(net.unix.api.modification.module.ModuleInfo.createByAnnotation(mainName, annotation).toString())
                        writer.flush()

                        writer.close()

                        return true
                    }
                }
            }
        }
    }

    private fun raiseError(message: String) {
        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, message)
    }

    private fun raiseError(message: String, element: Element) {
        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, message, element)
    }

    private fun fromClass(clazz: Class<*>): TypeMirror {
        return processingEnv.elementUtils.getTypeElement(clazz.name).asType()
    }

    private fun checkForNoArgsConstructor(mainPluginType: TypeElement) {
        val var3: Iterator<*> = ElementFilter.constructorsIn(mainPluginType.enclosedElements).iterator()

        while (var3.hasNext()) {
            val constructor = var3.next() as ExecutableElement
            if (constructor.parameters.isEmpty()) {
                return
            }
        }

        this.raiseError("Main module class must have a no argument constructor.", mainPluginType)
    }

}