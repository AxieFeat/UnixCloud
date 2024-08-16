package net.unix.api.modification

import javax.annotation.processing.AbstractProcessor
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.ElementFilter
import javax.tools.Diagnostic

abstract class CloudAnnotationProcessor : AbstractProcessor() {
    open fun raiseError(message: String, element: Element? = null) {
        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, message, element)
    }

    open fun fromClass(clazz: Class<*>): TypeMirror {
        return processingEnv.elementUtils.getTypeElement(clazz.name).asType()
    }

    open fun checkForNoArgsConstructor(mainType: TypeElement) {
        val var3: Iterator<*> = ElementFilter.constructorsIn(mainType.enclosedElements).iterator()

        while (var3.hasNext()) {
            val constructor = var3.next() as ExecutableElement
            if (constructor.parameters.isEmpty()) {
                return
            }
        }

        this.raiseError("Main modification class must have a no argument constructor.", mainType)
    }
}