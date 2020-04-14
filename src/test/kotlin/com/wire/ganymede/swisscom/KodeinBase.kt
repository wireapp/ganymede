package com.wire.ganymede.swisscom

import com.wire.ganymede.setup.bindConfiguration
import com.wire.ganymede.setup.configureContainer
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * Base for the tests that contains access to kodein.
 */
open class KodeinTestBase {
    /**
     * Direct Kodein Access.
     */
    protected val k = Kodein.direct {
        bindConfiguration()
        configureContainer()
    }

    /**
     * Obtains instance of the class.
     */
    protected inline fun <reified T : Any> instance(tag: Any? = null) = k.instance<T>(tag)
}
