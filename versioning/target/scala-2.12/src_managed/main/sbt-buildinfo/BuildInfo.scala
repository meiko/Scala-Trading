package fraxses.gateway.version

import scala.Predef._

/** This object was generated by sbt-buildinfo. */
case object BuildInfo {
  /** The value is "versioning". */
  val name: String = "versioning"
  /** The value is "18.02.26.2". */
  val version: String = "18.02.26.2"
  /** The value is "2.12.4". */
  val scalaVersion: String = "2.12.4"
  /** The value is "1.1.1". */
  val sbtVersion: String = "1.1.1"
  override val toString: String = {
    "name: %s, version: %s, scalaVersion: %s, sbtVersion: %s" format (
      name, version, scalaVersion, sbtVersion
    )
  }
}
