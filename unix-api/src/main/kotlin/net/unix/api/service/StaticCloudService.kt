package net.unix.api.service

/**
 * This type of services will not delete after UnixCloud stop.
 *
 * (But can still be deleted with [CloudService.delete])
 */
interface StaticCloudService : CloudService {

    /**
     * Is service static.
     *
     * If true - service will not delete after UnixCloud stop.
     * Else - service will be deleted after UnixCloud stop.
     */
    var static: Boolean

}