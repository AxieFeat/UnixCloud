package net.unix.api.remote

import java.io.Serializable
import java.rmi.Remote

/**
 * This interface represents remote accessible objects.
 */
interface RemoteAccessible : Remote, Serializable