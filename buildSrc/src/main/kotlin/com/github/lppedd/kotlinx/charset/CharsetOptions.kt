package com.github.lppedd.kotlinx.charset

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property

/**
 * @author Edoardo Luppi
 */
open class CharsetOptions(@get:Input val name: String, objectFactory: ObjectFactory) {
  /**
   * The aliases for the charset, to complement the canonical [name].
   */
  @get:Input
  val aliases: ListProperty<String> = objectFactory.listProperty<String>().convention(listOf())

  /**
   * The name of the generated declaration.
   *
   * Overrides the required [name] value.
   */
  @get:Input
  val className: Property<String> = objectFactory.property<String>().convention(name)

  /**
   * Whether the generated declaration should be outputted in the common source set.
   *
   * `false` by default.
   */
  @get:Input
  val common: Property<Boolean> = objectFactory.property<Boolean>().convention(false)
}
