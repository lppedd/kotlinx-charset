package com.github.lppedd.kotlinx.charset

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.property

/**
 * @author Edoardo Luppi
 */
open class EbcdicDbcsCharsetOptions(name: String, objectFactory: ObjectFactory) : CharsetOptions(name, objectFactory) {
  /**
   * The smallest legal second byte value, included.
   */
  @get:Input
  val b2Min: Property<Int> = objectFactory.property<Int>()

  /**
   * The largest legal second byte value, included.
   */
  @get:Input
  val b2Max: Property<Int> = objectFactory.property<Int>()
}
