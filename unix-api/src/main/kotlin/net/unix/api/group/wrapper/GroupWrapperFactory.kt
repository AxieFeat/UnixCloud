package net.unix.api.group.wrapper

import net.unix.api.pattern.Nameable
import net.unix.api.remote.RemoteAccessible
import net.unix.command.question.NextQuestion
import java.rmi.RemoteException
import java.util.concurrent.CompletableFuture

/**
 * This interface represents a factory of [GroupWrapper]'s.
 */
interface GroupWrapperFactory : Nameable, RemoteAccessible {

    /**
     * For what wrapper this factory.
     */
    @get:Throws(RemoteException::class)
    override val name: String

    /**
     * Create [GroupWrapper] by it serialized values.
     *
     * @param serialized Serialized wrapper.
     *
     * @return Instance of [GroupWrapper].
     */
    @Throws(RemoteException::class)
    fun createBySerialized(serialized: Map<String, Any>): GroupWrapper

    /**
     * Create question builder of this wrapper factory.
     *
     * @param context Context of builder. In it answer is name of this wrapper factory.
     *
     * @return Completable future instance of [GroupWrapper].
     */
    fun questionBuilder(context: NextQuestion<GroupWrapperFactory>): CompletableFuture<GroupWrapper>

}