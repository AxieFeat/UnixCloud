//package net.unix.api.command.sender
//
//import net.kyori.adventure.text.Component
//import net.unix.api.service.CloudService
//
///**
// * Command sender on [CloudService]
// */
//interface CloudCommandSender : CommandSender {
//
//    /**
//     * Current [CloudService]
//     */
//    val service: CloudService
//
//    /**
//     * Names of latest [CloudService]'s
//     */
//    val serviceHistory: List<String>
//
//    /**
//     * Send [Component] to command sender
//     *
//     * @param component [Component] instance
//     */
//    fun sendMessage(component: Component)
//
//    /**
//     * Send [Component] to command sender in actionbar
//     *
//     * @param component [Component] instance
//     */
//    fun sendActionbar(component: Component)
//
//    /**
//     * Send [Component] to command sender in title
//     *
//     * @param title Main title, [Component] instance
//     * @param subtitle Subtitle, [Component] instance
//     * @param fadeIn Fade in time
//     * @param fadeOut Fade out time
//     */
//    fun sendTitle(title: Component, subtitle: Component, fadeIn: Double = 1.0, fadeOut: Double = 1.0)
//}
//
