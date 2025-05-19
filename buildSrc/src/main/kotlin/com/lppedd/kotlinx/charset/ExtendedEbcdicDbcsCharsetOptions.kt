package com.lppedd.kotlinx.charset

import org.gradle.api.model.ObjectFactory

/**
 * @author Edoardo Luppi
 */
open class ExtendedEbcdicDbcsCharsetOptions(
  name: String,
  objectFactory: ObjectFactory,
) : EbcdicDbcsCharsetOptions(name, objectFactory)
